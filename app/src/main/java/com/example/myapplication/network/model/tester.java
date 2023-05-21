package com.example.myapplication.network.model;

import com.example.myapplication.network.util.encrypt;
import com.example.myapplication.repository.ConverterFactory;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import okhttp3.FormBody;
import retrofit2.Retrofit;

public class tester {

    public static void main(String[] args) {
        {
            try {
                // creating a string for
                // storing our extracted text.
                String extractedText = "";

                // creating a variable for pdf reader
                // and passing our PDF file in it.
                PdfReader reader = new PdfReader("res/raw/amiya_rout.pdf");

                // below line is for getting number
                // of pages of PDF file.
                int n = reader.getNumberOfPages();

                // running a for loop to get the data from PDF
                // we are storing that data inside our string.
                for (int i = 0; i < n; i++) {
                    extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                    // to extract the PDF content from the different pages
                }

                // after extracting all the data we are
                // setting that string value to our text view.
                System.out.println(extractedText);

                // below line is used for closing reader.
                reader.close();
            } catch (Exception e) {
                // for handling error while extracting the text file.
                System.out.println("Error found is : \n" + e);
            }

        }
    }
}