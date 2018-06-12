package com.sunbjx.demos.framework.tools.codec;

import com.sunbjx.demos.framework.tools.CharsetConst;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * AES 加密 解密工具
 *
 * @author sunbjx
 * @since 2018/6/12 15:33
 */
public class AES {

    private static final Logger LOGGER = LoggerFactory.getLogger(AES.class);

    private static final Map<String, AES> AES_CACHES = new HashMap<String, AES>();

    /**
     * 算法名称 － {@code AES}。
     */
    public static final String ALGORITHM = "AES";

    /**
     * 默认的128位时的密钥长度。
     */
    public static final int KEY_LENGTH = 16;

    /**
     * 默认的128位时的向量长度。
     */
    public static final int IV_LENGTH = KEY_LENGTH;

    /**
     * 根据给定的私密密钥和默认的 {@code AES/ECB/PKCS5Padding}填充方式，获得一个 {@code AES}。
     *
     * @param key 私密密钥。
     * @return 给定的私密密钥和默认的 {@code AES/ECB/PKCS5Padding}填充方式的 {@code AES}。
     */
    public static AES getInstance(byte[] key) {
        return getInstance(key, AESModeEnum.ECB_PKCS5);
    }

    /**
     * 根据给定的私密密钥和给定的填充方式，获得一个 {@code AES}。
     *
     * @param key  私密密钥。
     * @param mode 模式与填充方式。
     * @return 给定的私密密钥和给定的填充方式的 {@code AES}。
     */
    public static AES getInstance(byte[] key, AESModeEnum mode) {
        AES o = new AES(key, mode);
        return o;
    }

    /**
     * 根据给定的私密密钥、IV向量和默认的 {@code AES/CBC/PKCS5Padding} 填充方式，获得一个 {@code AES}。
     *
     * @param key 私密密钥。
     * @param iv  IV 向量。
     * @return 给定的私密密钥、IV向量和默认的 {@code AES/CBC/PKCS5Padding} 填充方式的 {@code AES}。
     */
    public static AES getInstance(byte[] key, byte[] iv) {
        return getInstance(key, iv, AESModeEnum.CBC_PKCS5);
    }

    /**
     * 根据给定的私密密钥、IV向量和指定的填充方式，获得一个 {@code AES}。
     *
     * @param key  私密密钥。
     * @param iv   IV向量。
     * @param mode 模式与填充方式。
     * @return 返回给定的私密密钥、IV向量和指定的填充方式的 {@code AES}。
     */
    public static AES getInstance(byte[] key, byte[] iv, AESModeEnum mode) {
        AES o = new AES(key, iv, mode);
        return o;
    }

    private final AESModeEnum mode;
    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    private Cipher enc;
    private Cipher dec;

    /**
     * 根据给定的私密密钥和默认的 {@code AES/ECB/PKCS5Padding}填充方式，构造一个新的 {@code AES}。
     *
     * @param key 私密密钥。
     */
    public AES(byte[] key) {
        this(key, AESModeEnum.ECB_PKCS5);
    }

    /**
     * 根据给定的私密密钥和给定的填充方式，构造一个新的 {@code AES}。
     *
     * @param key  私密密钥。
     * @param mode 模式与填充方式。
     */
    public AES(byte[] key, AESModeEnum mode) {
        validateKey(key);

        byte[] aesKey = ArrayUtils.subarray(key, 0, KEY_LENGTH);
        this.keySpec = new SecretKeySpec(aesKey, ALGORITHM);
        this.ivSpec = null;
        this.mode = mode;

        initEncryptionCipher();
        initDecryptionCipher();
    }

    /**
     * 根据给定的私密密钥、IV向量和默认的 {@code AES/CBC/PKCS5Padding} 填充方式，构造一个新的 {@code AES}。
     *
     * @param key 私密密钥。
     * @param iv  IV 向量。
     */
    public AES(byte[] key, byte[] iv) {
        this(key, iv, AESModeEnum.CBC_PKCS5);
    }

    /**
     * 根据给定的私密密钥、IV向量和指定的填充方式，构造一个新的 {@code AES}。
     *
     * @param key  私密密钥。
     * @param iv   IV向量。
     * @param mode 模式与填充方式。
     */
    public AES(byte[] key, byte[] iv, AESModeEnum mode) {
        validateKey(key);
        validateIV(iv);

        byte[] aesKey = ArrayUtils.subarray(key, 0, KEY_LENGTH);
        byte[] aesIV = ArrayUtils.subarray(iv, 0, IV_LENGTH);
        this.keySpec = new SecretKeySpec(aesKey, ALGORITHM);
        this.ivSpec = new IvParameterSpec(aesIV);
        this.mode = mode;

        initEncryptionCipher();
        initDecryptionCipher();
    }

    /**
     * 校验密钥格式。
     */
    private void validateKey(byte[] key) {
        if (key == null || key.length < KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("Invalid AES key length: %s bytes", key == null ? 0 : key.length));
        }
    }

    /**
     * 校验向量格式。
     */
    private void validateIV(byte[] iv) {
        if (iv == null || iv.length < IV_LENGTH) {
            throw new IllegalArgumentException(String.format("Wrong IV length: must be %s bytes long", IV_LENGTH));
        }
    }

    /**
     * 返回明文加密后的最大长度。
     *
     * @param sourceLen 明文长度。
     * @return 明文加密后的最大长度。
     */
    public int getCihperLength(int sourceLen) {
        int base = 0;
        if (!StringUtils.endsWith(mode.getName(), "NoPadding")) {
            base = 16;
        }
        int pad = sourceLen % 16;
        if (pad == 0) {
            return sourceLen + base;
        }
        return sourceLen - pad + base;
    }

    /**
     * 对指定的数据进行 {@code AES} 算法加密。如果加密发生错误，则返回长度为 0 的 {@code byte} 数组。
     * <p/>
     * 处理 {@code data} 缓冲区中的字节以及可能在上一次 {@code update}
     * 操作中已缓存的任何输入字节，其中应用了填充（如果请求）。结果存储在新缓冲区中。
     * <p/>
     * 结束时，此方法将把此类中的 {@code cipher} 加密对象重置为上一次调用 {@code init}
     * 初始化得到的状态。即重置该对象，可供加密或解密（取决于调用 init 时指定的操作模式）更多的数据。
     *
     * @param data 要加密的数据。
     * @return 加密后的数据。
     */
    public byte[] encrypt(byte[] data) {
        if (ArrayUtils.isEmpty(data)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        byte[] input;
        if (StringUtils.endsWith(mode.getName(), "NoPadding")) {
            input = padding(data);
        } else {
            input = data;
        }
        try {
            return enc.doFinal(input);
        } catch (Exception ex) {
            writeEncryptLogger(ex);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * 对指定的数据进行 {@code AES} 算法解密。如果解密发生错误，则返回长度为 0 的 {@code byte} 数组。
     * <p/>
     * 处理 {@code data} 缓冲区中的字节以及可能在上一次 {@code update}
     * 操作中已缓存的任何输入字节，其中应用了填充（如果请求）。结果存储在新缓冲区中。
     * <p/>
     * 结束时，此方法将把此类中的 {@code cipher} 加密对象重置为上一次调用 {@code init}
     * 初始化得到的状态。即重置该对象，可供加密或解密（取决于调用 init 时指定的操作模式）更多的数据。
     *
     * @param data 要解密的数据。
     * @return 解密后的数据。
     */
    public byte[] decrypt(byte[] data) {
        try {
            byte[] result = dec.doFinal(data);
            if (StringUtils.endsWith(mode.getName(), "NoPadding")) {
                int idx = result.length;
                for (int i = result.length; --i >= 0; ) {
                    if (result[i] == (byte) 0) {
                        idx = i;
                    } else {
                        break;
                    }
                }
                ArrayUtils.subarray(result, 0, idx);
            }
            return result;
        } catch (Exception ex) {
            writeDecryptLogger(ex);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * 使用 {@code AES} 算法对指定的数据进行加密操作。如果加密发生错误，则返回长度为 0 的 {@code byte} 数组。
     * <p/>
     * 处理 data 缓冲区中从 {@code offset} 开始（包含）的前 {@code length} 个字节以及可能在上一次 {@code update}
     * 操作过程中已缓存的任何输入字节，其中应用了填充（如果需要）。结果存储在新缓冲区中。
     * <p/>
     * 结束时，此方法将把此类中的 {@code cipher} 加密对象重置为上一次调用 {@code init}
     * 初始化得到的状态。即重置该对象，可供加密或解密（取决于调用 init 时指定的操作模式）更多的数据。
     *
     * @param data   输入缓冲区。
     * @param offset {@code data} 中输入开始位置的偏移量。
     * @param length 输入长度。
     * @return 存储结果的新缓冲区，即加密后的数据。
     */
    public byte[] encrypt(byte[] data, int offset, int length) {
        try {
            return enc.doFinal(data, offset, length);
        } catch (Exception ex) {
            writeEncryptLogger(ex);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * 使用 {@code AES} 算法对指定的数据进行解密操作。如果解密发生错误，则返回长度为 0 的 {@code byte} 数组。
     * <p/>
     * 处理 data 缓冲区中从 {@code offset} 开始（包含）的前 {@code length} 个字节以及可能在上一次 {@code update}
     * 操作过程中已缓存的任何输入字节，其中应用了填充（如果需要）。结果存储在新缓冲区中。
     * <p/>
     * 结束时，此方法将把此类中的 {@code cipher} 加密对象重置为上一次调用 {@code init}
     * 初始化得到的状态。即重置该对象，可供加密或解密（取决于调用 init 时指定的操作模式）更多的数据。
     *
     * @param data   输入缓冲区。
     * @param offset {@code data} 中输入开始位置的偏移量。
     * @param length 输入长度。
     * @return 存储结果的新缓冲区，即解密后的数据。
     */
    public byte[] decrypt(byte[] data, int offset, int length) {
        try {
            return dec.doFinal(data, offset, length);
        } catch (Exception ex) {
            LOGGER.error("decode packet error with len " + data.length);
            writeDecryptLogger(ex);
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * 使用 {@code AES} 算法对指定的数据进行加密操作。如果加密发生错误，则返回 0。
     * <p/>
     * 处理 {@code inputData} 缓冲区中从 {@code inputOffset} 开始（包含）的前 {@code inputLen}
     * 个字节以及可能在上一次 {@code update} 操作过程中已缓存的任何输入字节，其中应用了填充（如果需要）。结果存储在 {@code outputData}
     * 缓冲区中从 {@code outputOffset}（包含）开始的位置。
     * <p/>
     * 如果 {@code outputData} 缓冲区太小无法保存该结果，则返回 0。
     *
     * @param inputData    要加密的数据。
     * @param inputOffset  {@code inputData} 中输入开始位置的偏移量。
     * @param inputLen     输入长度。
     * @param outputData   保存结果的缓冲区。
     * @param outputOffset {@code outputData} 中存储结果的位置的偏移量。
     * @return {@code outputData} 中存储的字节数。
     */
    public int encrypt(byte[] inputData, int inputOffset, int inputLen, byte[] outputData, int outputOffset) {
        try {
            return enc.doFinal(inputData, inputOffset, inputLen, outputData, outputOffset);
        } catch (Exception ex) {
            writeEncryptLogger(ex);
        }
        return 0;
    }

    /**
     * 使用 {@code AES} 算法对指定的数据进行解密操作。如果解密发生错误，则返回 0。
     * <p/>
     * 处理 {@code inputData} 缓冲区中从 {@code inputOffset} 开始（包含）的前 {@code inputLen}
     * 个字节以及可能在上一次 {@code update} 操作过程中已缓存的任何输入字节，其中应用了填充（如果需要）。结果存储在 {@code outputData}
     * 缓冲区中从 {@code outputOffset}（包含）开始的位置。
     * <p/>
     * 如果 {@code outputData} 缓冲区太小无法保存该结果，则返回 0。
     *
     * @param inputData    要解密的数据。
     * @param inputOffset  {@code inputData} 中输入开始位置的偏移量。
     * @param inputLen     输入长度。
     * @param outputData   保存结果的缓冲区。
     * @param outputOffset {@code outputData} 中存储结果的位置的偏移量。
     * @return {@code outputData} 中存储的字节数。
     */
    public int decrypt(byte[] inputData, int inputOffset, int inputLen, byte[] outputData, int outputOffset) {
        try {
            return dec.doFinal(inputData, inputOffset, inputLen, outputData, outputOffset);
        } catch (Exception ex) {
            writeDecryptLogger(ex);
        }
        return 0;
    }

    /**
     * 加密给定的数据，并返回密文转换成16进制的字符串。如果加密失败，则返回 {@code null}。
     *
     * @param data 要加密的数据。
     * @return 返回加密密文转换成16进制的字符串。
     */
    public String encryptToHex(byte[] data) {
        byte[] bytes = encrypt(data);
        if (bytes.length == 0) {
            return null;
        }
        return Hex.encodeHexString(bytes);
    }

    /**
     * 解密给定的16进制字符串，返回明文数据。如果解密失败则返回空的{@code byte}数组。
     * <p/>
     * 该方法用于解密 {@link #encryptToHex(byte[])} 方法加密后的16进制字符串。
     *
     * @param hexStr 16进制字符串表示的密文。
     * @return 返回明文数据。
     */
    public byte[] decryptHex(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        byte[] bytes;
        try {
            bytes = Hex.decodeHex(hexStr.toCharArray());
        } catch (DecoderException ex) {
            writeDecryptLogger(ex);
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return decrypt(bytes);
    }

    /**
     * 使用指定的字符编码，加密给定的字符串，返回密文数据。如果加密失败则返回空的 {@code byte} 数组。
     *
     * @param input    要加密的字符串。
     * @param encoding 字符编码。
     * @return 返回密文数据。
     */
    public byte[] encryptString(String input, String encoding) {
        if (StringUtils.isBlank(input)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = CharsetConst.ISO_8859_1_STR;
        }
        byte[] data;
        try {
            data = input.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            writeEncryptLogger(ex);
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return encrypt(data);
    }

    /**
     * 使用指定的字符编码对给定的数据进行解密，还原成原字符串。
     * 如果 {@code encoding == null || "".equals(encoding)}，则字符编码默认为 {@code ISO-889-1}
     * <p/>
     * 如果 {@code data} 为{@code null} 或空数组，或者解密失败，则返回 {@code null}。 该方法主要用于解密由
     * {@link #encryptString(String, String)} 加密后的数据。
     *
     * @param data     要解密的数据。
     * @param encoding 字符编码。
     * @return 解密后的原文字符串。
     */
    public String decryptToString(byte[] data, String encoding) {
        if (ArrayUtils.isEmpty(data)) {
            return null;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = CharsetConst.ISO_8859_1_STR;
        }
        byte[] result = decrypt(data);
        if (result.length == 0) {
            return null;
        }
        try {
            return new String(result, encoding);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    /**
     * 使用指定的字符编码，加密给定的字符串，并返回密文转换成16进制的字符串。如果加密失败，则返回 {@code null}。
     * <p/>
     * 如果 {@code encoding == null || "".equals(encoding)}，则字符编码为 {@code ISO-8859-1}。
     *
     * @param input    要加密的字符串。
     * @param encoding 字符编码。
     * @return 返回加密后的密文16进制字符串。
     */
    public String encryptStringToHex(String input, String encoding) {
        byte[] bytes = encryptString(input, encoding);
        if (bytes.length == 0) {
            return null;
        }
        return Hex.encodeHexString(bytes);
    }

    /**
     * 解密给定的16进制字符串，返回明文字符串。如果解密失败则返回 {@code null}。
     * <p/>
     * 该方法用于解密 {@link #encryptStringToHex(String, String)} 方法加密后的16进制字符串密文。
     *
     * @param hexStr   16进制字符串表示的密文。
     * @param encoding 字符编码。
     * @return 返回明文字符串。
     */
    public String decryptHexToString(String hexStr, String encoding) {
        byte[] bytes = decryptHex(hexStr);
        if (bytes.length == 0) {
            return null;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = CharsetConst.ISO_8859_1_STR;
        }
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    /**
     * 加密给定的数据，将密文数据进行 {@code base64} 编码。如果给定的数据为空或加密失败，则返回 {@code null}。
     *
     * @param data 要加密的数据。
     * @return 返回AES加密后的密文的 {@code base64} 编码。
     */
    public String encryptToBase64(byte[] data) {
        byte[] bytes = encrypt(data);
        if (bytes.length == 0) {
            return null;
        }
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将给定的 {@code base64} 编码格式字符串还原成 {@code byte} 数组，
     * 然后对该数据进行 {@code AES} 解密。如果解密失败，则返回空的{@code byte}数组。
     * <p/>
     * 该方法用于解密 {@link #encryptToBase64(byte[])} 方法加密后的字符串。
     *
     * @param base64Str base64格式字符串。
     * @return 返回解密后的数据。
     */
    public byte[] decryptBase64(String base64Str) {
        byte[] base64Bytes = Base64.decodeBase64(base64Str);
        if (ArrayUtils.isEmpty(base64Bytes)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return decrypt(base64Bytes);
    }

    /**
     * 使用指定的字符编码加密给定的字符串。
     * <p/>
     * 该方法先将明文字符串根据指定字符编码转换成 {@code byte[]}，然后调用 {@link #encryptToBase64(byte[])} 进行数据加密。
     * 如果 {@code encoding == null || "".equals(encoding)}，则字符编码默认为 {@code ISO-8859-1}。
     *
     * @param input    要加密的字符串。
     * @param encoding 字符编码。
     * @return 返回加密后的密文字符串。
     */
    public String encryptStringToBase64(String input, String encoding) {
        byte[] bytes = encryptString(input, encoding);
        if (bytes.length == 0) {
            return null;
        }
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将给定的 {@code base64} 编码格式字符串还原成 {@code byte} 数组，
     * 然后对该数据进行 {@code AES} 解密，返回明文字符串。如果解密失败，则返回 {@code null}。
     * <p/>
     * 该方法是对 {@link #decryptBase64(String)} 的補充。用于对
     * {@link #encryptStringToBase64(String, String)} 方法加密后的 字符串解密。
     * <p>
     * <pre>
     * String plain = "I am a good boy.";
     * String encoding = CharEncoding.UTF_8;
     * String enc_plain = encryptToBase64(plain, encoding);
     * String dec_plain = decryptToBase64(enc_plain, encoding);
     * dec_plain = "I am a good boy."
     * </pre>
     *
     * @param base64Str 要解密的经过 {@code base64} 编码的密文。
     * @param encoding  字符编码。
     * @return 返回解密后的字符串。
     */
    public String decryptBase64ToString(String base64Str, String encoding) {
        byte[] bytes = decryptBase64(base64Str);
        if (bytes.length == 0) {
            return null;
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = CharsetConst.ISO_8859_1_STR;
        }
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    private void writeEncryptLogger(Throwable cause) {
        LOGGER.error(String.format("AES encrypt failed. Cause: %s", cause.getMessage()));
    }

    private void writeDecryptLogger(Throwable cause) {
        LOGGER.error(String.format("AES decrypt failed. Cause: %s", cause.getMessage()));
    }

    private byte[] padding(byte[] bytes) {
        int len = bytes.length;
        if (len == 0) {
            return bytes;
        }
        int pad = 0;
        if (len < 16) {
            pad = 16 - len;
            byte[] pads = createSequence((byte) 0, pad, (byte) 0);
            return ArrayUtils.addAll(bytes, pads);
        } else {
            pad = len % 16;
            if (pad != 0) {
                byte[] pads = createSequence((byte) 0, 16 - pad, (byte) 0);
                return ArrayUtils.addAll(bytes, pads);
            }
            return bytes;
        }
    }

    private void initEncryptionCipher() {
        if (enc == null) {
            try {
                enc = Cipher.getInstance(mode.getName());
                if (ivSpec == null) {
                    enc.init(Cipher.ENCRYPT_MODE, keySpec);
                } else {
                    enc.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
                }
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.error(String.format("The current platform no such algorithm - %s, Cause: %s", ALGORITHM, ex.getMessage()));
            } catch (NoSuchPaddingException ex) {
                LOGGER.error(String.format("The current platform no such padding - %s, Cause: %s", mode.getName(), ex.getMessage()));
            } catch (InvalidKeyException ex) {
                LOGGER.error(StringUtils.join(new Object[]{"Invalid key - ", (keySpec != null ? keySpec.toString() : "encrypt key. "),
                        " Cause: ", ex.getMessage()}));
            } catch (InvalidAlgorithmParameterException ex) {
                LOGGER.error(StringUtils.join(new Object[]{"Invalid algorithm parameter - ",
                        (ivSpec != null ? ivSpec.toString() : "encrypt iv parameter. "), "Cause: ",
                        ex.getMessage()}));
            }
        }
    }

    private void initDecryptionCipher() {
        if (dec == null) {
            try {
                dec = Cipher.getInstance(mode.getName());
                if (ivSpec == null) {
                    dec.init(Cipher.DECRYPT_MODE, keySpec);
                } else {
                    dec.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                }
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.error(String.format("The current platform no such algorithm - %s, Cause: %s", ALGORITHM, ex.getMessage()));
            } catch (NoSuchPaddingException ex) {
                LOGGER.error(String.format("The current platform no such padding - %s, Cause: %s", mode.getName(), ex.getMessage()));
            } catch (InvalidKeyException ex) {
                LOGGER.error(StringUtils.join(new Object[]{"Invalid key - ", (keySpec != null ? keySpec.toString() : "decrypt key. "),
                        " Cause: ", ex.getMessage()}));
            } catch (InvalidAlgorithmParameterException ex) {
                LOGGER.error(StringUtils.join(new Object[]{"Invalid algorithm parameter - ",
                        (ivSpec != null ? ivSpec.toString() : "decrypt iv parameter. "), "Cause: ",
                        ex.getMessage()}));
            }
        }
    }

    private static byte[] createSequence(byte start, int length, byte step) {
        byte[] result = new byte[length];
        if (step == 0) {
            for (int i = 0; i < length; i++) {
                result[i] = start;
            }
            return result;
        }
        for (int i = 0; i < length; i++) {
            result[i] = start;
            int next = (int) start + (int) step;
            if (next < Byte.MIN_VALUE || next > Byte.MAX_VALUE) {
                // 正序
                if (step > 0) {
                    start = Byte.MAX_VALUE;
                    step = 0;
                } else {
                    start = Byte.MIN_VALUE;
                    step = 0;
                }
            } else {
                start = Integer.valueOf(next).byteValue();
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        final String encoding = "UTF-8";
        final String plain = "`1234567890~!@#$%^&*()_+|{}:\"<>?[];',./'\\\n\r\t中国人";
        final byte[] plainBytes = plain.getBytes(encoding);
        System.out.println(String.format("原文字节数组长度：%s", plainBytes.length));
        final byte[] key = "0123456789ABCDEF".getBytes();
        final byte[] iv = "FEDCBA9876543210".getBytes();
        AES ecb_aes = new AES(key);
        System.out.println("ECB+++++++++++++++++++++++++++++++++ Base64 ++++++++++++++++++++++++++++++++++++");
        String b64Str = ecb_aes.encryptStringToBase64(plain, encoding);
        String b64Plain = ecb_aes.decryptBase64ToString(b64Str, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", b64Str));
        System.out.println(String.format("解密：%s", b64Plain));
        System.out.println("");
        System.out.println("ECB+++++++++++++++++++++++++++++++++ Hex(16) +++++++++++++++++++++++++++++++++++");
        String hexStr = ecb_aes.encryptStringToHex(plain, encoding);
        String hexPlain = ecb_aes.decryptHexToString(hexStr, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", hexStr));
        System.out.println(String.format("解密：%s", hexPlain));
        System.out.println("");
        AES cbc_aes = new AES(key, iv, AESModeEnum.CBC_NO);
        System.out.println("CBC+++++++++++++++++++++++++++++++++ Base64 ++++++++++++++++++++++++++++++++++++");
        String b64Str2 = cbc_aes.encryptStringToBase64(plain, encoding);
        String b64Plain2 = cbc_aes.decryptBase64ToString(b64Str2, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", b64Str2));
        System.out.println(String.format("解密：%s", b64Plain2));
        System.out.println("");
        System.out.println("CBC+++++++++++++++++++++++++++++++++ Hex(16) +++++++++++++++++++++++++++++++++++");
        String hexStr2 = cbc_aes.encryptStringToHex(plain, encoding);
        String hexPlain2 = cbc_aes.decryptHexToString(hexStr2, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", hexStr2));
        System.out.println(String.format("解密：%s", hexPlain2));
        System.out.println("");
        AES cbc_aes_pkcs5 = new AES(key, iv, AESModeEnum.CBC_PKCS5);
        System.out.println("CBC+CBC_PKCS5+++++++++++++++++++++++ Base64 ++++++++++++++++++++++++++++++++++++");
        String b64Str3 = cbc_aes_pkcs5.encryptStringToBase64(plain, encoding);
        String b64Plain3 = cbc_aes_pkcs5.decryptBase64ToString(b64Str3, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", b64Str3));
        System.out.println(String.format("解密：%s", b64Plain3));
        System.out.println("");
        System.out.println("CBC+CBC_PKCS5+++++++++++++++++++++++ Hex(16) +++++++++++++++++++++++++++++++++++");
        String hexStr3 = cbc_aes_pkcs5.encryptStringToHex(plain, encoding);
        String hexPlain3 = cbc_aes_pkcs5.decryptHexToString(hexStr3, encoding);
        System.out.println(String.format("原文：%s", plain));
        System.out.println(String.format("密文：%s", hexStr3));
        System.out.println(String.format("解密：%s", hexPlain3));
        System.out.println("");
    }
}
