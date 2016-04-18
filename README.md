# GoogleLoginHelper

# How to use

add this 3 dependencies into gradle
    ```
    compile 'com.google.android.gms:play-services-identity:8.3.0'
    compile 'com.google.android.gms:play-services-plus:8.3.0'
    compile 'com.google.android.gms:play-services-auth:8.3.0'
    ```
Copy GoogleLoginHelper.java into your project

# Example
  ```
  import ...
  
  public class MainActivity extends AppCompatActivity implements GoogleLoginHelper.OnRetrieveDataListener {

    Button button;
    ImageView image;
    TextView name, email;

    GoogleLoginHelper googleLoginHelper;

    @Override
    protected void onStart() {
        super.onStart();
        googleLoginHelper.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleLoginHelper.revokeGplusAccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        image = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        googleLoginHelper = new GoogleLoginHelper(this, this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLoginHelper.revokeGplusAccess();
                googleLoginHelper.signInWithGplus();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleLoginHelper.setOnActivityResult(resultCode, resultCode, data);
    }

    @Override
    public void onRetrieveData(String id, String name, String email, String image_url) {
        this.name.setText(name);
        this.email.setText(email);
        Picasso.with(this).load(image_url).into(image);
    }
}
```
