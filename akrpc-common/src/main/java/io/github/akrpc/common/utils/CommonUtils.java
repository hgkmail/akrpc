package io.github.akrpc.common.utils;

import com.google.common.base.Splitter;
import io.github.akrpc.common.constant.MagicValue;
import io.github.akrpc.common.dto.ServiceAddress;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 通用工具类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/16
 */
public class CommonUtils {

    private CommonUtils() {}

    /**
     * 解析服务地址字符串
     */
    public static ServiceAddress parseServiceAddress(String address, String defaultHost, int defaultPort) {
        List<String> addressParts = Splitter.on(MagicValue.COLON).trimResults().splitToList(address);

        String host = StringUtils.isNotBlank(addressParts.getFirst()) ? addressParts.getFirst() : defaultHost;
        int port = addressParts.size() > 1 ? Integer.parseInt(addressParts.get(1)) : defaultPort;

        return new ServiceAddress(host, port);
    }

}
