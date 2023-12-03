package com.immanlv.trem.network.util

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object encrypt {
    fun getSha512(input: String): String {
        return try {
            // getInstance() method is called with algorithm SHA-512
            val md = MessageDigest.getInstance("SHA-512")

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            val messageDigest = md.digest(input.toByteArray())

            // Convert byte array into signum representation
            val no = BigInteger(1, messageDigest)

            // Convert message digest into hex value
            var hashtext = no.toString(16)

            // Add preceding 0s to make it 32 bit
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }

            // return the HashText
            hashtext
        } // For specifying wrong message digest algorithms
        catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    fun getMd5(input: String): String {
        return try {

            // Static getInstance method is called with hashing MD5
            val md = MessageDigest.getInstance("MD5")

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            val messageDigest = md.digest(input.toByteArray())

            // Convert byte array into signum representation
            val no = BigInteger(1, messageDigest)

            // Convert message digest into hex value
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } // For specifying wrong message digest algorithms
        catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(getSha512(getSha512("2101229079" + "#" + "god is with us") + "#" + "62f1f28e97a3f"))
    }

    fun gg() {}
}