/*
 * Copyright 2015 Groupon.com
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
package models.internal;

/**
 * Alert condition operators.
 *
 * @author Ville Koskela (ville dot koskela at inscopemetrics dot com)
 */
public enum Operator {

    /**
     * Equal to.
     */
    EQUAL_TO,

    /**
     * Not equal to.
     */
    NOT_EQUAL_TO,

    /**
     * Less than.
     */
    LESS_THAN,

    /**
     * Less than or equal to.
     */
    LESS_THAN_OR_EQUAL_TO,

    /**
     * Greater than.
     */
    GREATER_THAN,

    /**
     * Greater than or equal to.
     */
    GREATER_THAN_OR_EQUAL_TO;
}
