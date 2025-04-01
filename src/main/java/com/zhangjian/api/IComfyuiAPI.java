package com.zhangjian.api;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

/**
 * comfyui的api
 */
public interface IComfyuiAPI {

    /**
     * 提交工作流
     * @param body
     * @return
     */
    @POST("/prompt")
    @Headers({"content-type: application/json"})
    Call<JsonNode> prompt(@Body String body);

    /**
     * 工作流执行历史
     * @param promptId
     * @return
     */
    @GET("/history/{promptId}")
    @Headers({"content-type: application/json"})
    Call<JsonNode> history(@Path("promptId") String promptId);

    /**
     * 下载文件二进制数据
     * @param subfolder
     * @param filename
     * @param type
     * @return
     */
    @GET("/view")
    @Headers({"content-type: application/json"})
    Call<ResponseBody> view(@Query("subfolder") String subfolder, @Query("filename") String filename, @Query("type")String type);

}
