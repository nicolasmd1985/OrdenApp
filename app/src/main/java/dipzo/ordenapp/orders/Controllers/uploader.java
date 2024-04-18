package dipzo.ordenapp.orders.Controllers;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;


public class uploader {

    String
            ACCESS_KEY = "AKIAU6GD3GA2M6HUWMTC",
            SECRET_KEY = "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";




    Context context;

    public uploader(Context context) {
        super();
        this.context = context;
    }

    public void uploadtos3 (final Context context, final File file) {

        if(file !=null){
            // Inicializar el proveedor de credenciales de Amazon Cognito
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,

                    "us-west-2:3f1008b8-f534-41d0-928f-f9264e27ae76", // ID del grupo de identidades
                    Regions.US_WEST_2 // Región
            );

            AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);



            AmazonS3 s3 = new AmazonS3Client(credentials);


            TransferUtility transferUtility = new TransferUtility(s3, context);
            final TransferObserver observer = transferUtility.upload(
                    "deploy-from-github",  //this is the bucket name on S3
                    file.getName(),
                    file,
                    CannedAccessControlList.PublicRead //to make the file public
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                    } else if (state.equals(TransferState.FAILED)) {
                        Toast.makeText(context,"Failed to upload",Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }
    }

}
