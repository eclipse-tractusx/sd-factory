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

package org.eclipse.tractusx.selfdescriptionfactory.service.wallet;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import io.vavr.Function1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Slf4j
@Service
@RequestScope
public class CustodianWallet {

    private final CustodianClient custodianClient;
    private Function1<String, Map<String, Object>> walletInfoFn;

    public CustodianWallet(CustodianClient custodianClient) {
        this.custodianClient = custodianClient;
        walletInfoFn = ((Function1<String, Map<String, Object>>)custodianClient::getWalletData).memoized();
    }

    public VerifiableCredential getSignedVC(VerifiableCredential objToSign) {
        return custodianClient.getSignedVC(objToSign);
    }

    public Map<String, Object> getWalletData(String bpnNumber) {
        return walletInfoFn.apply(bpnNumber);
    }

}