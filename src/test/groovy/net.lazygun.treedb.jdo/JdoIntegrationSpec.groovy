package net.lazygun.treedb.jdo

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.jdo.JDOHelper
import javax.jdo.PersistenceManagerFactory

/**
 * @author Ewan
 */
@Stepwise
class JdoIntegrationSpec extends Specification {

    @Shared private PersistenceManagerFactory pmf;
    @Shared private JdoTreeDb db;

    def setupSpec() {
        pmf = JDOHelper.getPersistenceManagerFactory("Test")
    }

    def "JdoTreeDb is initialised on construction"() {
        when:
            db = new JdoTreeDb(pmf)

        then:
            db.branches().length() == 1
            db.branches().first().completeHistory().length() == 2
    }
}
