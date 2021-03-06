/**
 * cdp4j Commercial License
 *
 * Copyright 2017, 2020 WebFolder OÜ
 *
 * Permission  is hereby  granted,  to "____" obtaining  a  copy of  this software  and
 * associated  documentation files  (the "Software"), to deal in  the Software  without
 * restriction, including without limitation  the rights  to use, copy, modify,  merge,
 * publish, distribute  and sublicense  of the Software,  and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  IMPLIED,
 * INCLUDING  BUT NOT  LIMITED  TO THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL  THE AUTHORS  OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.webfolder.cdp.channel;

import static java.lang.Boolean.FALSE;

import java.net.URI;
import java.net.URISyntaxException;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocketConnectOptions;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.session.MessageHandler;
import io.webfolder.cdp.session.SessionFactory;

public class VertxWebSocketFactory implements ChannelFactory {

    private final HttpClient httpClient;

    // https://cs.chromium.org/chromium/src/content/browser/devtools/devtools_http_handler.cc?type=cs&q=kSendBufferSizeForDevTools&sq=package:chromium&g=0&l=83
    public static final int MAX_PAYLOAD_SIZE = 256 * 1024 * 1024; // 256Mb

    public VertxWebSocketFactory(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Channel createChannel(Connection     connection,
                                 SessionFactory factory,
                                 MessageHandler handler) {
        WebSocketConnection webSocketConnection = (WebSocketConnection) connection;
        WebSocketConnectOptions options = new WebSocketConnectOptions();
        URI uri = null;
        try {
            uri = new URI(webSocketConnection.getUrl());
        } catch (URISyntaxException e) {
            throw new CdpException(e);
        }
        options.setHost(uri.getHost());
        options.setPort(uri.getPort());
        options.setURI(uri.getPath());
        options.setSsl(FALSE);
        VertxWebSocketChannel channel = new VertxWebSocketChannel(factory, httpClient, handler, options);
        return channel;
    }
}
