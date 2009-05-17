/*
 Copyright (c) 2008 thePlatform, Inc.

This file is part of Cuanto, a test results repository and analysis program.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package cuanto.parsers

import cuanto.ParsableTestOutcome
import cuanto.ParsableTestCase

/**
 * User: Todd Wells
 * Date: Oct 24, 2008
 * Time: 2:05:32 PM
 * 
 */
class TestNgParser implements CuantoTestParser{


	public List<ParsableTestOutcome> parseFile(File file) {
		parseStream(file.newInputStream())
	}


	public String getTestType() {
		return "TestNG"
	}


	public List<ParsableTestOutcome> parseStream(InputStream stream) {
		def parsableOutcomes = []

		def xml = new XmlParser().parse(stream)

		xml.suite.each { suite->
			suite.test.each { test->

				test.'class'.each { testClass ->
				    testClass.'test-method'.each { testMethod ->
					    def out = new ParsableTestOutcome()

					    def resultText = testMethod.'@status'

					    if (resultText == "FAIL") {
						    out.testResult = "Fail"
					    } else if (resultText == "PASS") {
						    out.testResult = "Pass"
					    } else if (resultText == "SKIP") {
						    out.testResult = "Skip"
					    }

					    out.testCase = new ParsableTestCase()
					    out.testCase.packageName = testClass.'@name'
					    out.testCase.fullName = testClass.'@name' + "." + testMethod.'@name'
					    out.testCase.testName = testMethod.'@name'
					    out.testCase.description = testMethod.@description
					    out.duration = Integer.valueOf(testMethod.'@duration-ms')
					    if (testMethod.exception.size()) {
						    out.testOutput = testMethod.exception[0].'full-stacktrace'.text()
					    }
					    parsableOutcomes << out
				    }
				}
			}
		}
		return parsableOutcomes
	}

}