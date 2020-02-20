package mahecha.nicolas.elcaaplicacion.Controllers;

import android.content.Context;
import android.widget.Toast;

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

    Context context;

    public uploader(Context context) {
        super();
        this.context = context;
    }

    public void uploadtos3 (final Context context, final File file) {

        if(file !=null){
            CognitoCachingCredentialsProvider credentialsProvider;
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "us-east-1:8f0038be-624e-483d-93d3-c15a88f84d23", // ID del grupo de identidades
                    Regions.US_EAST_1 // Región
            );

            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);


            TransferUtility transferUtility = new TransferUtility(s3, context);
            final TransferObserver observer = transferUtility.upload(
                    "ordenappbucket",  //this is the bucket name on S3
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
