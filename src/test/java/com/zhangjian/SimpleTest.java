package com.zhangjian;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zhangjian.api.IComfyuiAPI;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SimpleTest {

    @Test
    public void test()throws Exception{
        System.out.println(System.getProperties());
    }

    @Test
    public void apitest() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request().newBuilder()
                                .addHeader("X-Pinggy-No-Screen", "X-Pinggy-No-Screen")
                                .build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://rnoai-35-202-36-148.a.free.pinggy.link/").
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        IComfyuiAPI comfyuiAPI = retrofit.create(IComfyuiAPI.class);

        String clientId = UUID.randomUUID().toString().replace("-","");
        System.out.println("clientId = "+clientId);
        String req = "{\n" +
                "  \"client_id\": \"98300168214448d78c724a5508ebaff7\",\n" +
                "  \"prompt\": {\n" +
                "    \"1\": {\n" +
                "      \"inputs\": {\n" +
                "        \"sd_type\": \"SDXL\",\n" +
                "        \"ckpt_name\": \"SDXL/sd_xl_base_1.0.safetensors\",\n" +
                "        \"character_weights\": \"none\",\n" +
                "        \"lora\": \"none\",\n" +
                "        \"lora_scale\": 0.8,\n" +
                "        \"trigger_words\": \"best quality\",\n" +
                "        \"scheduler\": \"Euler\",\n" +
                "        \"model_type\": \"txt2img\",\n" +
                "        \"id_number\": 2,\n" +
                "        \"sa32_degree\": 0.5,\n" +
                "        \"sa64_degree\": 0.5,\n" +
                "        \"img_width\": 768,\n" +
                "        \"img_height\": 768\n" +
                "      },\n" +
                "      \"class_type\": \"Storydiffusion_Model_Loader\",\n" +
                "      \"_meta\": {\n" +
                "        \"title\": \"Storydiffusion_Model_Loader\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "      \"inputs\": {\n" +
                "        \"info\": [\n" +
                "          \"1\",\n" +
                "          1\n" +
                "        ],\n" +
                "        \"character_prompt\": \"[Taylor] a woman img, wearing a white T-shirt, blue loose hair.\\n[Lecun] a man img,wearing a suit,black hair.\",\n" +
                "        \"scene_prompts\": \"[Taylor]wake up in the bed,medium shot;\\n[Taylor]have breakfast by the window;\\n[Lecun] drving on the road,medium shot;\\n[Lecun]work in the company.\",\n" +
                "        \"split_prompt\": \"\",\n" +
                "        \"negative_prompt\": \"bad anatomy, bad hands, missing fingers, extra fingers, three hands, three legs, bad arms, missing legs, missing arms, poorly drawn face, bad face, fused face, cloned face, three crus, fused feet, fused thigh, extra crus, ugly fingers, horn,amputation, disconnected limbs\",\n" +
                "        \"img_style\": \"No_style\",\n" +
                "        \"seed\": 1185510095,\n" +
                "        \"steps\": 20,\n" +
                "        \"cfg\": 7,\n" +
                "        \"ip_adapter_strength\": 0.5,\n" +
                "        \"style_strength_ratio\": 20,\n" +
                "        \"encoder_repo\": \"laion/CLIP-ViT-bigG-14-laion2B-39B-b160k\",\n" +
                "        \"role_scale\": 0.8,\n" +
                "        \"mask_threshold\": 0.5,\n" +
                "        \"start_step\": 5,\n" +
                "        \"save_character\": false,\n" +
                "        \"controlnet_model_path\": \"none\",\n" +
                "        \"controlnet_scale\": 0.8,\n" +
                "        \"layout_guidance\": false,\n" +
                "        \"pipe\": [\n" +
                "          \"1\",\n" +
                "          0\n" +
                "        ]\n" +
                "      },\n" +
                "      \"class_type\": \"Storydiffusion_Sampler\",\n" +
                "      \"_meta\": {\n" +
                "        \"title\": \"Storydiffusion_Sampler\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"3\": {\n" +
                "      \"inputs\": {\n" +
                "        \"filename_prefix\": \"ComfyUI\",\n" +
                "        \"images\": [\n" +
                "          \"2\",\n" +
                "          0\n" +
                "        ]\n" +
                "      },\n" +
                "      \"class_type\": \"SaveImage\",\n" +
                "      \"_meta\": {\n" +
                "        \"title\": \"保存图像\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        retrofit2.Response<JsonNode> execute = comfyuiAPI.prompt(req).execute();
        String promptId = execute.body().get("prompt_id").asText();
//        retrofit2.Response<JsonNode> hisResponse = comfyuiAPI.history(promptId).execute();
//        System.out.println(hisResponse.body().get(promptId).get("status").get("completed").asText());
//        boolean completed = hisResponse.body().get(promptId).get("status").get("completed").asBoolean(false);
//        if(completed){
//            JsonNode images = hisResponse.body().get(promptId).get("outputs").get("3").get("images");
//            System.out.println("===images==");
//            System.out.println(images.toPrettyString());
//            if (images.isArray()){
//                ArrayNode arrayNode = (ArrayNode) images;
//                for (int i = 0; i < arrayNode.size(); i++) {
//                    String filename = arrayNode.get(i).get("filename").asText();
//                    retrofit2.Response<ResponseBody> imgFile = comfyuiAPI.view(arrayNode.get(i).get("subfolder").asText(),
//                            filename,
//                            arrayNode.get(i).get("type").asText()).execute();
//                    if(imgFile.isSuccessful()){
//                        FileOutputStream fos = new FileOutputStream(new File(filename));
//                        fos.write(imgFile.body().bytes());
//                        fos.flush();
//                        fos.close();
//                    }
//                }
//            }
//        }

    }
}
