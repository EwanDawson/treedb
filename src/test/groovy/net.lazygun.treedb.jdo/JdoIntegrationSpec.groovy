package net.lazygun.treedb.jdo

import net.lazygun.treedb.BranchNotFoundException
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

    def "Initialise JdoTreeDb on construction"() {
        when:
        db = new JdoTreeDb(pmf)

        then:
        db.branches().length() == 1
        db.branches().first().completeHistory().length() == 2
    }

    def "Create new branch"() {
        when:
        final parentBranch = db.branches().head()
        db.createBranch("MyBranch", parentBranch.tip().parent())

        then:
        db.branches().length() == 2
        db.getBranch("MyBranch") != null
    }

    def "Rename a branch"() {
        given:
        final int numBranches = db.branches().length()
        final branch = db.getBranch("MyBranch") as JdoBranch
        final oldVersion = branch.version()

        when:
        db.renameBranch(branch, "MyUpdatedBranch")

        then:
        final updatedBranch = db.getBranch("MyUpdatedBranch") as JdoBranch
        updatedBranch != null
        updatedBranch.version() == oldVersion + 1

        and: 'the total number of branches is the same as before'
        db.branches().length() == numBranches
    }

    def "Delete a branch"() {
        when:
        db.deleteBranch(db.getBranch("MyBranch"))
        db.getBranch("MyBranch")

        then:
        thrown(BranchNotFoundException)
    }
}
