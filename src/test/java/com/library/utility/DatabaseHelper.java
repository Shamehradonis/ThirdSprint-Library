package com.library.utility;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DatabaseHelper {


    public static String getBookByIdQuery(String bookID) {
        return "select * from books where id="+bookID;
    }

    public static String getCategoryIdQuery(String categoryName) {
        return "select id from book_categories where name='"+categoryName+"'";
    }

    public static String getUserByIdQuery(int id) {
        return "select full_name,email,user_group_id,status,start_date,end_date,address " +
                "from users where id="+id;
    }
    public static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}