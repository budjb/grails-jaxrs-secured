/*
 * Copyright 2013 Bud Byrd
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
package com.budjb.jaxrs.security

class JaxrsClient {
    /**
     * Api Key
     */
    String apiKey

    /**
     * Client name
     */
    String name

    /**
     * Whether the key is active
     */
    boolean active = true

    /**
     * Date the key was created
     */
    Date dateCreated

    /**
     * Field constraints.
     */
    static constraints = {
        name blankable: false
        apiKey blankable: false
        dateCreated nullable: true
    }
}
