import org.junit.jupiter.api.*
import step.*
import testutil.*
import testutil.cmdRunner
import testutil.setupTest
import java.nio.file.*
import org.assertj.core.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BamtoSignalTests {

    @BeforeAll fun setup() = setupTest()
    @AfterAll fun cleanup() = cleanupTest()

    @Test fun `test stranded signal generation`() {

        cmdRunner.bamtosignal(
            SignalParameters(
                bam = getResourcePath("test.bam"),
                chromosomeSizes = getResourcePath("chrom.sizes"),
                outputDirectory = testDir,
                stranded = true,
                outputPrefix = "stranded"
            )
        )

        assertThat(testDir.resolve("stranded_minusAll.bw")).exists()
        assertThat(testDir.resolve("stranded_minusAll.bw").toFile().md5())
            .isEqualTo("ebe7ec1e26273ce59081e5b481075832")
        assertThat(testDir.resolve("stranded_minusUniq.bw")).exists()
        assertThat(testDir.resolve("stranded_minusUniq.bw").toFile().md5())
            .isEqualTo("ebe7ec1e26273ce59081e5b481075832")

        assertThat(testDir.resolve("stranded_plusAll.bw")).exists()
        assertThat(testDir.resolve("stranded_plusAll.bw").toFile().md5())
            .isEqualTo("e671d20dd2e22fbef92e09b4dd890567")
        assertThat(testDir.resolve("stranded_plusUniq.bw")).exists()
        assertThat(testDir.resolve("stranded_plusUniq.bw").toFile().md5())
            .isEqualTo("e671d20dd2e22fbef92e09b4dd890567")

   }

   @Test fun `test unstranded signal generation`() {

        cmdRunner.bamtosignal(
            SignalParameters(
                bam = getResourcePath("test.bam"),
                chromosomeSizes = getResourcePath("chrom.sizes"),
                outputDirectory = testDir,
                stranded = false,
                outputPrefix = "unstranded"
            )
        )

        assertThat(testDir.resolve("unstranded_All.bw")).exists()
        assertThat(testDir.resolve("unstranded_All.bw").toFile().md5())
            .isEqualTo("ebe7ec1e26273ce59081e5b481075832")
        assertThat(testDir.resolve("unstranded_Uniq.bw")).exists()
        assertThat(testDir.resolve("unstranded_Uniq.bw").toFile().md5())
            .isEqualTo("ebe7ec1e26273ce59081e5b481075832")

    }

}
