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
package org.apache.sedona.common.geometrySerde;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.Serializable;
import org.ade.SpatialFHE.spatialfhe.*;
import org.apache.sedona.common.fheSerde.TFHEBoolSerializer;
import org.locationtech.jts.geom.Envelope;

/**
 * Provides methods to efficiently serialize and deserialize encrypted geometry types.
 *
 * <p>Supports TFHEPoint, TFHELineString, TFHEPolygon, and Envelope types.
 *
 * <p>First byte contains {@link Type#id}. Then go type-specific bytes, followed by user-data
 * attached to the geometry.
 */
public class CipherGeometrySerde extends Serializer implements Serializable {
  @Override
  public void write(Kryo kryo, Output out, Object object) {
    if (object instanceof TFHEGeometry) {
      TFHEGeometry geometry = (TFHEGeometry) object;
      writeType(out, Type.SHAPE);
      writeGeometry(kryo, out, geometry);
    } else if (object instanceof Envelope) {
      Envelope envelope = (Envelope) object;
      writeType(out, Type.ENVELOPE);
      out.writeDouble(envelope.getMinX());
      out.writeDouble(envelope.getMaxX());
      out.writeDouble(envelope.getMinY());
      out.writeDouble(envelope.getMaxY());
    } else if (object instanceof TFHEBool) {
      writeType(out, Type.TFHEBOOL);
      TFHEBool tfheBool = (TFHEBool) object;
      byte[] data = TFHEBoolSerializer.serialize(tfheBool);
      out.writeInt(data.length);
      out.write(data, 0, data.length);
      //    } else if (object instanceof TFHEInt) {
      //      writeType(out, Type.TFHEINT);
      //      TFHEInt32 tfheInt = (TFHEInt32) object;
      //      byte[] data = CipherGeometrySerializer.serialize(tfheInt);
      //      out.writeInt(data.length);
      //      out.write(data, 0, data.length);
    } else {
      throw new UnsupportedOperationException(
          "Cannot serialize object of type " + object.getClass().getName());
    }
  }

  private void writeType(Output out, Type type) {
    out.writeByte((byte) type.id);
  }

  private void writeGeometry(Kryo kryo, Output out, TFHEGeometry geometry) {
    byte[] data = CipherGeometrySerializer.serialize(geometry);
    out.writeInt(data.length);
    out.write(data, 0, data.length);
    //    writeUserData(kryo, out, geometry);
  }

  //  private void writeUserData(Kryo kryo, Output out, TFHEGeometry geometry) {
  //    Object userData = geometry.getUserData();
  //    out.writeBoolean(userData != null);
  //    if (userData != null) {
  //      kryo.writeClass(out, userData.getClass());
  //      kryo.writeObject(out, userData);
  //    }
  //  }

  @Override
  public Object read(Kryo kryo, Input input, Class aClass) {
    byte typeId = input.readByte();
    Type geometryType = Type.fromId(typeId);
    switch (geometryType) {
      case SHAPE:
        return readGeometry(kryo, input);
      case ENVELOPE:
        {
          double xMin = input.readDouble();
          double xMax = input.readDouble();
          double yMin = input.readDouble();
          double yMax = input.readDouble();
          return new Envelope(xMin, xMax, yMin, yMax);
        }
      case TFHEBOOL:
        return TFHEBoolSerializer.deserialize(input.readBytes(input.readInt()));

      default:
        throw new UnsupportedOperationException(
            "Cannot deserialize object of type " + geometryType);
    }
  }

  private Object readUserData(Kryo kryo, Input input) {
    Object userData = null;
    if (input.readBoolean()) {
      Registration clazz = kryo.readClass(input);
      userData = kryo.readObject(input, clazz.getType());
    }
    return userData;
  }

  private TFHEGeometry readGeometry(Kryo kryo, Input input) {
    int length = input.readInt();
    byte[] bytes = new byte[length];
    input.readBytes(bytes);
    TFHEGeometry geometry = CipherGeometrySerializer.deserialize(bytes);
    //    geometry.setUserData(readUserData(kryo, input));
    return geometry;
  }

  private enum Type {
    SHAPE(0),
    ENVELOPE(1),
    TFHEBOOL(2),
    TFHEINT(3);

    private final int id;

    Type(int id) {
      this.id = id;
    }

    public static Type fromId(int id) {
      for (Type type : values()) {
        if (type.id == id) {
          return type;
        }
      }

      return null;
    }
  }
}
