package com.zhangjian.controller;


import com.jayway.jsonpath.JsonPath;
import com.zhangjian.BaseTest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class BaseControllerTest extends BaseTest {

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mvc;

    @Before
    public void before() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testEcho() throws Exception{
        String req = "{\n" +
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
                "  }\n";

        mvc.perform(MockMvcRequestBuilders.post("/workflow/prompt").header("content-type","application/json").content(req)
        ).andDo(print()).andExpect(status().isOk()).andDo(mvcResult -> {
            String promptId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.promptId");
            mvc.perform(get("/workflow/view/"+promptId)
            ).andExpect(status().isOk()).andDo(mvcResult1 -> {
                String header = mvcResult1.getResponse().getHeader("Content-Disposition");
                String fileName = Arrays.stream(header.split(";")).filter(s -> s.startsWith("filename=")).findFirst().get().replace("filename=", "");
                IOUtils.write(mvcResult1.getResponse().getContentAsByteArray(),new FileOutputStream(System.getProperty("java.io.tmpdir")+fileName));
            }).andReturn();
        }).andReturn();

    }

}
