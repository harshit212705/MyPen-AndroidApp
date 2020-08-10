package application.example.mypen;

import com.google.gson.JsonObject;

import application.example.mypen.GenerateFontResponse;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


interface APIInterface {

    @POST("/api/generate_font")
    @Multipart
    Call<GenerateFontResponse> getGenerateFontBodyResponse(
            @Part MultipartBody.Part[] charImages,
            @Part("font_name") String font_name
    );


    @POST("/api/generate_document")
    @Multipart
    Call<GenerateDocumentResponse> getGenerateDocumentBodyResponse(
            @Part("document_name") String document_name,
            @Part("font_file_content64") String font_file,
            @Part("font_size") String font_size,
            @Part("font_ink_color") String font_ink_color,
            @Part("paper_margin") String paper_margin,
            @Part("paper_lines") String paper_lines,
            @Part("text_file_content64") String text_file
    );


}
