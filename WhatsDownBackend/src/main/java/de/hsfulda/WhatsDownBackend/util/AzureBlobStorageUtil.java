package de.hsfulda.WhatsDownBackend.util;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AzureBlobStorageUtil {
    @Value("${azure.storage.account-name}")
    private String accountName;
    @Value("${azure.storage.sas-key}")
    private String sasKey;
    @Value("${azure.storage.connection-string}")
    private String azureStorageConnectionString;

    public String generateSasToken(String blobName, String containerName) {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, sasKey);

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint("https://" + accountName + ".blob.core.windows.net")
                    .credential(credential)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            BlobSasPermission permission = new BlobSasPermission()
                    .setReadPermission(true);

            BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(OffsetDateTime.now().plusHours(1), permission)
                    .setStartTime(OffsetDateTime.now());

            return blobClient.generateSas(sasSignatureValues);
        } catch (Exception e) {
            log.error("Error while generating SAS token: {}", e.getMessage());
        }
        return null;
    }

    public String uploadMedia(MultipartFile file, String containerName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureStorageConnectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        String blobName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes()) {
        }) {
            BlockBlobClient blobClient = containerClient.getBlobClient(blobName).getBlockBlobClient();
            blobClient.upload(stream, file.getSize());
        } catch (IOException e) {
            log.error("Error while uploading media: {}", e.getMessage());
        }

        return containerClient.getBlobClient(blobName).getBlobUrl();
    }

    public String generateSasTokenFromUrl(String blobUrl, String containerName) {
        String blobName = parseBlobName(blobUrl);
        return generateSasToken(blobName, containerName);
    }

    public String parseBlobName(String blobUrl) {
        int lastSlashIndex = blobUrl.lastIndexOf("/");
        if (lastSlashIndex != -1 && lastSlashIndex < blobUrl.length() - 1) {
            return blobUrl.substring(lastSlashIndex + 1);
        }
        return null;
    }
}
