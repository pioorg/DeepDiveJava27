/*
 *  Copyright (C) 2025 Piotr Przybył
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.przybyl.ddj27.lazyConstants;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/// don't forget `--enable-preview`
public class CurrentWeatherTeller {

    private final LazyConstant<HttpClient> httpClient =
        LazyConstant.of(() ->
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build());

    private HttpClient getHttpClient() {
        return httpClient.get();
    }

    void main() {
        Stream.of(
                "https://api.open-meteo.com/v1/forecast?latitude=50.45&longitude=30.52&current_weather=true", //Kyiv
                "https://api.open-meteo.com/v1/forecast?latitude=52.23&longitude=21.01&current_weather=true" //Warsaw
            )
            .gather(Gatherers.mapConcurrent(5, uri -> HttpRequest.newBuilder().uri(URI.create(uri)).build()))
            .gather(Gatherers.mapConcurrent(5, this::sendHttpReq))
            .map(HttpResponse::body)
            .forEach(IO::println);
    }

    private HttpResponse<String> sendHttpReq(HttpRequest req) {
        try {
            return getHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
