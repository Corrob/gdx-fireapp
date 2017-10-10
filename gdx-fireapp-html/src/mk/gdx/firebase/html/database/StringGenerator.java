/*
 * Copyright 2017 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mk.gdx.firebase.html.database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Transforms data to string.
 */
public class StringGenerator
{
    private static final Array<Class> PRIMITIVES = new Array<Class>(new Class[]{
            Number.class, Integer.class, String.class,
            Float.class, Double.class, Boolean.class
    });

    /**
     * Returns JSON string representation of object.
     * <p>
     * It using libgdx {@link Json} class.
     *
     * @param object Any object
     * @return JSON string representation of {@code object}
     */
    public static String dataToString(Object object)
    {
        if (isPrimitiveType(object))
            return object.toString();
        Json json = new Json();
        json.setTypeName(null);
        json.setQuoteLongValues(true);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);
        return json.toJson(object);
    }

    private static boolean isPrimitiveType(Object object)
    {
        return PRIMITIVES.contains(object.getClass(), true);
    }
}