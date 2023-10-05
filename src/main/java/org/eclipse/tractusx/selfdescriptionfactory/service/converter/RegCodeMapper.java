/********************************************************************************
 * Copyright (c) 2022,2023 T-Systems International GmbH
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.selfdescriptionfactory.service.converter;

import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.RegistrationNumberSchema.TypeEnum;

import java.util.Map;

public class RegCodeMapper {
    private RegCodeMapper(){}
    public static Map<TypeEnum, String> getRegCodeMapper(String prefix) {
        return Map.of(
                TypeEnum.TAXID, prefix.concat("local"),
                TypeEnum.VATID, prefix.concat("vatID"),
                TypeEnum.EUID, prefix.concat("EUID"),
                TypeEnum.EORI, prefix.concat("EORI"),
                TypeEnum.LEICODE, prefix.concat("leiCode")
        );
    }
}
