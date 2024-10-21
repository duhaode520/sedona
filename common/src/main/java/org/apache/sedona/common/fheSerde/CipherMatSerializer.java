/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sedona.common.fheSerde;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.ade.SpatialFHE.FHEHelper;
import org.ade.SpatialFHE.spatialfhe.CipherMat;
import org.ade.SpatialFHE.spatialfhe.CipherText;
import org.ade.SpatialFHE.spatialfhe.CipherTextVector;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class CipherMatSerializer {
  private CipherMatSerializer() {}

  private static class CipherMatKryoSerializer extends Serializer<CipherMat> {
    @Override
    public void write(Kryo kryo, Output output, CipherMat cipherMat) {
      output.writeInt(cipherMat.getWidth());
      output.writeInt(cipherMat.getHeight());
      output.writeInt(cipherMat.getData().size());
      for (CipherText ct : cipherMat.getData()) {
        String str = ct.toString();
        output.writeString(str);
      }
    }

    @Override
    public CipherMat read(Kryo kryo, Input input, Class<CipherMat> aClass) {
      int width = input.readInt();
      int height = input.readInt();
      int size = input.readInt();
      CipherText[] cipherTexts = new CipherText[size];
      FHEHelper fheHelper = FHEHelper.getInstance();
      for (int i = 0; i < size; i++) {
        String str = input.readString();
        cipherTexts[i] = fheHelper.getManager().buildCipherText(str);
      }
      return new CipherMat(width, height, new CipherTextVector(cipherTexts));
    }
  }

  public static ThreadLocal<Kryo> kryos =
      ThreadLocal.withInitial(
          () -> {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(
                new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            try {
              kryo.register(CipherMat.class, new CipherMatKryoSerializer());
            } catch (Exception e) {
              throw new RuntimeException("Cannot register kryo serializer for class CipherMat", e);
            }
            return kryo;
          });

  public static byte[] serialize(CipherMat cipherMat) {
    Kryo kryo = kryos.get();
    try (Output output = new Output(1024, -1)) {
      kryo.writeObject(output, cipherMat);
      return output.toBytes();
    }
  }

  public static CipherMat deserialize(byte[] bytes) {
    Kryo kryo = kryos.get();
    try (Input input = new Input(bytes)) {
      return kryo.readObject(input, CipherMat.class);
    }
  }
}
