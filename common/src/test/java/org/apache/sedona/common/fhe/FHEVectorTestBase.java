package org.apache.sedona.common.fhe;

import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.spatialfhe.HECrypto;
import org.junit.Before;

public abstract class FHEVectorTestBase {
    protected static final String resourceFolder =
            System.getProperty("user.dir") + "/../spark/common/src/test/resources/";
    String fhelibPath =
            "/home/ubuntu/projects/SpatialFHE/build/SpatialFHE/java/SpatialFHE-linux-x86-64/src/main/resources/SpatialFHE-linux-x86-64/libjniSpatialFHE.so";
    String fheJsonConfig =
            "{"
                    + "\"SchemeType\": \"CKKS\","
                    + "\"PolyModulusDegree\": 8192,"
                    + "\"PlaintextModulus\": 0,"
                    + "\"CoeffModulusPrimes\": [],"
                    + "\"CoeffModulusBits\": [60, 30, 30, 30, 60],"
                    + "\"ScaleFactor\": 30"
                    + "}";

    @Before
    public void setup() {
        FHEHelper.getOrCreate(
                resourceFolder + "tmp/public.key",
                resourceFolder + "tmp/private.key",
                fhelibPath,
                fheJsonConfig,
                "127.0.0.1:8080",
                HECrypto.HELibrary.Phantom,
                true,
                2);
    }

}
