package f;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.content.FileProvider;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.secure.itsonfire.R;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.io.FilesKt__FileReadWriteKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.Charsets;
import kotlin.text.StringsKt___StringsKt;
import org.jetbrains.annotations.NotNull;

@StabilityInferred(parameters = 0)
@Metadata(bv = {}, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u001a\u0010\u001bJ*\u0010\n\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00022\b\u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\bH\u0002J\u0010\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\u0004H\u0002J\u0010\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0018\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u001a\u0010\u0017\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J\u0016\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u0011¨\u0006\u001c"}, d2 = {"Lf/b;", "", "", "algorithm", "", "input", "Ljavax/crypto/spec/SecretKeySpec;", "key", "Ljavax/crypto/spec/IvParameterSpec;", "iv", "b", "value", "", "a", "Landroid/content/Context;", "context", GoogleApiAvailabilityLight.TRACKING_SOURCE_DIALOG, "", "resourceId", "Ljava/io/File;", "c", "Landroid/content/res/Resources;", "res", "e", "Landroid/content/Intent;", "f", "<init>", "()V", "app_release"}, k = 1, mv = {1, 6, 0})
/* loaded from: classes.dex */
public final class b {

    /* renamed from: a */
    @NotNull
    public static final b f360a = new b();

    /* renamed from: b */
    public static final int f361b = 0;

    private b() {
    }

    /* renamed from: a */
    private final long return_crc32(byte[] value) {
        CRC32 crc32 = new CRC32();
        crc32.update(value);
        return crc32.getValue();
    }

    /* renamed from: b */
    private final byte[] decryptBytes(String algorithm, byte[] input, SecretKeySpec key, IvParameterSpec iv) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(2, key, iv);
        byte[] bArrDoFinal = cipher.doFinal(input);
        Intrinsics.checkNotNullExpressionValue(bArrDoFinal, "cipher.doFinal(input)");
        return bArrDoFinal;
    }

    /* renamed from: c */
    private final File manipulateFile(int resourceId, Context context) throws Throwable {
        Resources resources = context.getResources();
        Intrinsics.checkNotNullExpressionValue(resources, "context.resources");
        byte[] resById = getResById(resources, resourceId);
        String strManipulateStrings = manipulateStrings(context);
        Charset charset = Charsets.UTF_8;
        byte[] bytes = strManipulateStrings.getBytes(charset);
        Intrinsics.checkNotNullExpressionValue(bytes, "this as java.lang.String).getBytes(charset)");
        SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, context.getString(R.string.ag)); // AES
        String string = context.getString(R.string.alg); // AES/CBC/PKCS5Padding
        Intrinsics.checkNotNullExpressionValue(string, "context.getString(R.string.alg)");
        String string2 = context.getString(R.string.iv); // iv.png path
        Intrinsics.checkNotNullExpressionValue(string2, "context.getString(\n     …             R.string.iv)");
        byte[] bytes2 = string2.getBytes(charset);
        Intrinsics.checkNotNullExpressionValue(bytes2, "this as java.lang.String).getBytes(charset)");
        byte[] bArrDecryptBytes = decryptBytes(string, resById, secretKeySpec, new IvParameterSpec(bytes2));
        File file = new File(context.getCacheDir(), context.getString(R.string.playerdata));
        FilesKt__FileReadWriteKt.writeBytes(file, bArrDecryptBytes);
        return file;
    }

    /* renamed from: d */
    private final String manipulateStrings(Context context) {
        String string = context.getString(R.string.c2); // https://flare-on.com/evilc2server/report_token/report_token.php?token=
        Intrinsics.checkNotNullExpressionValue(string, "context.getString(R.string.c2)");
        String string2 = context.getString(R.string.w1); // wednesday
        Intrinsics.checkNotNullExpressionValue(string2, "context.getString(R.string.w1)");
        StringBuilder sb = new StringBuilder();
        sb.append(string.subSequence(4, 10)); // sb = s://fl
        sb.append(string2.subSequence(2, 5)); // sb = s://flnte
        String string3 = sb.toString(); // string3 = s://flnte
        Intrinsics.checkNotNullExpressionValue(string3, "StringBuilder().apply(builderAction).toString()");
        byte[] bytes = string3.getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "this as java.lang.String).getBytes(charset)");
        long jReturn_crc32 = return_crc32(bytes);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(jReturn_crc32);
        sb2.append(jReturn_crc32); // sb2 = crc32_result * 2
        String string4 = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string4, "StringBuilder().apply(builderAction).toString()");
        return StringsKt___StringsKt.slice(string4, new IntRange(0, 15)); // string4 = (crc32_result * 2)[0, 15]
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.content.res.Resources] */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.InputStream, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* renamed from: e */
    private final byte[] getResById(Resources res, int resourceId) throws Throwable {
        Throwable th;
        InputStream inputStreamOpenRawResource;
        byte[] bArr = null;
        try {
            try {
                inputStreamOpenRawResource = res.openRawResource(resourceId);
            } catch (IOException e2) {
                e = e2;
                inputStreamOpenRawResource = null;
            } catch (Throwable th2) {
                res = 0;
                th = th2;
                try {
                    Intrinsics.checkNotNull(res);
                    res.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                throw th;
            }
            try {
                byte[] bArr2 = new byte[inputStreamOpenRawResource.available()];
                inputStreamOpenRawResource.read(bArr2);
                try {
                    Intrinsics.checkNotNull(inputStreamOpenRawResource);
                    inputStreamOpenRawResource.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                bArr = bArr2;
                res = inputStreamOpenRawResource;
            } catch (IOException e5) {
                e = e5;
                e.printStackTrace();
                try {
                    Intrinsics.checkNotNull(inputStreamOpenRawResource);
                    inputStreamOpenRawResource.close();
                    res = inputStreamOpenRawResource;
                } catch (IOException e6) {
                    e6.printStackTrace();
                    res = e6;
                }
                return bArr;
            }
            return bArr;
        } catch (Throwable th3) {
            th = th3;
            Intrinsics.checkNotNull(res);
            res.close();
            throw th;
        }
    }

    @NotNull
    public final Intent f(@NotNull Context context, int resourceId) throws Throwable {
        Intrinsics.checkNotNullParameter(context, "context");
        Uri uriForFile = FileProvider.getUriForFile(context, Intrinsics.stringPlus(context.getApplicationContext().getPackageName(), context.getString(R.string.prdr)), manipulateFile(resourceId, context));
        Intent intent = new Intent(context.getString(R.string.aias));
        intent.addFlags(32768);
        intent.addFlags(268435456);
        intent.addFlags(1);
        intent.setType(context.getString(R.string.mime));
        intent.putExtra(context.getString(R.string.es), uriForFile);
        return intent;
    }
}
