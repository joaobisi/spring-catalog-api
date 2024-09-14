import { S3Client, GetObjectCommand, PutObjectCommand } from '@aws-sdk/client-s3';

const client = new S3Client({ region: 'us-east-2' });

export const handler = async (event) => {
  try {
    for (const record of event.Records) {
      console.log("Iniciando processamento da mensagem ", record);
      
      const rawBody = JSON.parse(record.body);
      const body = JSON.parse(rawBody.Message);
      const ownerId = body.ownerId;
      const operationType = body.operationType;
      
      delete body.operationType;
      
      try {
        var bucketName = "catalogmarketplace";
        var fileName = `${ownerId}-catalog.json`;
        const catalog = await getS3Object(bucketName, fileName);
        const catalogData = JSON.parse(catalog);

        switch (operationType) {
            case 'CREATE':
              if (body.type === 'product') {
                createItem(catalogData.products, body);
              } else {
                createItem(catalogData.categories, body);
              }
                console.log('Creating:', body);
                break;
            case 'UPDATE':
                 if (body.type === 'product') {
                    updateItem(catalogData.products, body);
                  } else {
                    updateItem(catalogData.categories, body);
                  }
                console.log('Updating:', body);
                break;
            case 'DELETE':
                 if (body.type === 'product') {
                    deleteItem(catalogData.products, body);
                  } else {
                    deleteItem(catalogData.categories, body);
                  }
                console.log('Deleting:', body);
                break;
            default:
                console.log('Tipo de operação desconhecido:', body.operationType);
                break;
        }
        
        await putS3Object(bucketName, fileName, JSON.stringify(catalogData));

      } catch (error) {
        if (error.message.includes("Error getting object from bucket")) {
          const newCatalog = { products: [], categories: [] };
          if (body.type === 'product') {
            newCatalog.products.push(body);
          } else {
            newCatalog.categories.push(body);
          }
          await putS3Object(bucketName, fileName, JSON.stringify(newCatalog));
        } else {
          throw error;
        }
      }
    }

    return { status: "success" };

  } catch (error) {
    console.error("Error processing message from SQS:", error);
    throw new Error("Error processing message from SQS");
  }
};

async function getS3Object(bucket, key) {
  const getCommand = new GetObjectCommand({
    Bucket: bucket,
    Key: key
  });

  try {
    const response = await client.send(getCommand);
    
    return streamToString(response.Body);
  } catch (error) {
    throw new Error("Error getting object from bucket");
  }
}

async function putS3Object(destBucket, destKey, content) {
  try {
    const putCommand = new PutObjectCommand({
      Bucket: destBucket,
      Key: destKey,
      Body: content,
      ContentType: "application/json"
    });

    const putResult = await client.send(putCommand);
    
    return putResult;

  } catch (error) {
    console.error("Error putting object on S3:", error);
    throw new Error("Error putting object on S3");
  }
}

function createItem(catalog, newItem) {
  const index = catalog.findIndex(item => item.id === newItem.id);
  
  if (index === -1) {
    catalog.push(newItem);
  }
}

function updateItem(catalog, newItem) {
  const index = catalog.findIndex(item => item.id === newItem.id);
  
  if (index !== -1) {
    catalog[index] = { ...catalog[index], ...newItem };
  }
}

function deleteItem(catalog, itemToDelete) {
  const index = catalog.findIndex(item => item.id === itemToDelete.id);

  if (index !== -1) {
    catalog.splice(index, 1);
  }
}

function streamToString(stream) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    stream.on('data', (chunk) => chunks.push(chunk));
    stream.on('end', () => resolve(Buffer.concat(chunks).toString('utf-8')));
    stream.on('error', reject);
  });
}
