package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;

import com.hypelabs.hype.Instance;

/**
 * This class represents an Hype client.
 */
public class Client
{
    /**
     * Hype instance of the client
     */
    final Instance instance;

    /**
     * Key of the client. The key is generated by hashing the instance identifier using the
     * {@value HpsConstants#HASH_ALGORITHM} algorithm.
     */
    byte key[];

    /**
     * Client class constructor.
     *
     * @param instance Hype instance of the client object to be created.
     * @throws NoSuchAlgorithmException This exception is thrown when the
     *          {@value HpsConstants#HASH_ALGORITHM} algorithm is not available on the device.
     */
    public Client(Instance instance) throws NoSuchAlgorithmException
    {
        this.instance = instance;
        this.key = HpsGenericUtils.getByteArrayHash(instance.getIdentifier());
    }
}
