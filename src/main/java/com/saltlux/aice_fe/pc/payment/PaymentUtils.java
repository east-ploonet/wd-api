package com.saltlux.aice_fe.pc.payment;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class PaymentUtils {
    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String sha256WithRSA(String input, String strPk,String privateKeyPassword) throws Exception {
        // Remove markers and new line characters in private key
        String realPK = strPk.replaceAll("-----END ENCRYPTED PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN ENCRYPTED PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        String signData = null;
        PrivateKey priKey = null;
        priKey = loadSplMctPrivateKeyPKCS8(realPK,privateKeyPassword);

        byte[] btArrTargetData = input.getBytes( StandardCharsets.UTF_8 );
        try {
            Signature sign = Signature.getInstance( "SHA256WithRSA" );
            sign.initSign( priKey );
            sign.update( btArrTargetData );

            byte[] btArrSignData = sign.sign();

            signData = Base64.getEncoder().encodeToString( btArrSignData );

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return signData;
    }

    public static String sha512(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static String generateOid(Long idUser)
    {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String buyerNumber = null;

        if (idUser != null)
            buyerNumber = String.format("%05x", idUser.intValue()).toUpperCase();  // 유저 ID 16진수 변경

        return "O_" + LocalDateTime.now().format(FORMATTER) + "_" + (idUser != null ? buyerNumber : "000000");

    }

    public static PrivateKey loadSplMctPrivateKeyPKCS8(String strPriKeyData,  String privateKeyPassword)
    {
        PrivateKey priKey = null;

        // 개인키 인증서 경로 및 비밀번호(테스트용)

        try
        {
            /*********************************
             * PKCS#8 foramt 에서 header & footer 제거
             *********************************/


            // Base64 decoding
            byte[] btArrPriKey   = Base64.getDecoder().decode( strPriKeyData );

            /*********************************
             * PEMParser 적용 항목 선언
             *********************************/
            ASN1Sequence derSeq = ASN1Sequence.getInstance( btArrPriKey );
            PKCS8EncryptedPrivateKeyInfo encPkcs8PriKeyInfo = new PKCS8EncryptedPrivateKeyInfo( org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo.getInstance( derSeq ) );

            /*********************************
             * 복호화 & Key 변환
             *********************************/
            JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter();
            InputDecryptorProvider decProvider = new JceOpenSSLPKCS8DecryptorProviderBuilder().build( privateKeyPassword.toCharArray() );

            PrivateKeyInfo priKeyInfo          = encPkcs8PriKeyInfo.decryptPrivateKeyInfo( decProvider );
            priKey                             = pemKeyConverter.getPrivateKey( priKeyInfo );

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (OperatorCreationException e)
        {
            e.printStackTrace();
        }
        catch (PKCSException e)
        {
            e.printStackTrace();
        }

        return priKey;
    }
}
