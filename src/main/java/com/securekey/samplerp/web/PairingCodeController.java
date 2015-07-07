/* Copyright (c) 2014, 2015 SecureKey Technologies Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.securekey.samplerp.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Get pairing code for the user (from index.html)
 * 
 * @author      Fedja Stevanovic (fedja.stevanovic@securekey.com)
 * 
 */

@Controller @RequestMapping("pair.html")
public class PairingCodeController {

	@RequestMapping(method = RequestMethod.GET)
	public String pair (Model model, @RequestParam(required = false) String userId) {

		return "pair";
	}


}
