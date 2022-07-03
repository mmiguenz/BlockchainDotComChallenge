import com.blockchaindotcom.providers.DeliveryProvider.apiServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val logger: Logger = LoggerFactory.getLogger("Main")
    logger.info("Starting main application ...")

    apiServer.start()
}