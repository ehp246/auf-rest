package me.ehp246.test.embedded;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * @author Lei Yang
 *
 */
@Suite
@SuiteDisplayName("Integration with embedded REST controllers")
@SelectPackages("me.ehp246.test.embedded")
class EmbeddedTestSuite {

}
