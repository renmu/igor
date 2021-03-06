/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.igor.scm.stash.client

import com.netflix.spinnaker.igor.config.StashConfig
import com.netflix.spinnaker.igor.scm.stash.client.model.CompareCommitsResponse
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Tests that Stash stashClient correctly binds to underlying model as expected
 */
class StashClientSpec extends Specification {

    @Shared
    StashClient client

    @Shared
    MockWebServer server

    void setup() {
        server = new MockWebServer()
    }

    void cleanup() {
        server.shutdown()
    }

    private void setResponse(String body) {
        server.enqueue(
            new MockResponse()
                .setBody(body)
                .setHeader('Content-Type', 'text/xml;charset=UTF-8')
        )
        server.start()
        client = new StashConfig().stashClient(server.getUrl('/').toString(), 'username', 'password')
    }

    void 'getCompareCommits'() {
        given:
        setResponse getCompareCommitsResponse()

        when:
        CompareCommitsResponse commitsResponse = client.getCompareCommits('foo', 'repo', [toCommit:'abcd', fromCommit:'defg'])

        then:
        commitsResponse.size == 2
        commitsResponse.isLastPage == false
        commitsResponse.start == 0
        commitsResponse.limit == 2
        commitsResponse.nextPageStart == 2
        commitsResponse.values.size() == 2

        commitsResponse.values[0].id == "adc708bb1251ac8177474d6a1b40f738f2dc44dc"
        commitsResponse.values[0].displayId == "adc708bb125"
        commitsResponse.values[0].author.name == "jcoder"
        commitsResponse.values[0].author.emailAddress == "jcoder@code.com"
        commitsResponse.values[0].author.id == 1817
        commitsResponse.values[0].author.displayName == "Joe Coder"
        commitsResponse.values[0].author.active == true
        commitsResponse.values[0].author.slug == "jcoder"
        commitsResponse.values[0].author.type == "NORMAL"
        commitsResponse.values[0].authorTimestamp == 1432081865000
        commitsResponse.values[0].message == "don't call evaluate if user is null"
        commitsResponse.values[0].parents[0].id == "70a121a7e8f86c54467a43bd29066e5ff1174510"
        commitsResponse.values[0].parents[0].displayId == "70a121a7e8f"

        commitsResponse.values[1].id == "70a121a7e8f86c54467a43bd29066e5ff1174510"
        commitsResponse.values[1].displayId == "70a121a7e8f"
        commitsResponse.values[1].author.name == "jcoder"
        commitsResponse.values[1].author.emailAddress == "jcoder@code.com"
        commitsResponse.values[1].author.id == 1817
        commitsResponse.values[1].author.displayName == "Joe Coder"
        commitsResponse.values[1].author.active == true
        commitsResponse.values[1].author.slug == "jcoder"
        commitsResponse.values[1].author.type == "NORMAL"
        commitsResponse.values[1].authorTimestamp == 1432081404000
        commitsResponse.values[1].message == "Merge branch 'my-work' into master"
        commitsResponse.values[1].parents[0].id == "3c3b942b09767e01c25e42bcb65a6630e8b2fc75"
        commitsResponse.values[1].parents[0].displayId == "3c3b942b097"
        commitsResponse.values[1].parents[1].id == "13881c94156429084910e6ca417c48fcb6d74be8"
        commitsResponse.values[1].parents[1].displayId == "13881c94156"
    }

    String getCompareCommitsResponse() {
        return '\n' +
            '{"values":[{"id":"adc708bb1251ac8177474d6a1b40f738f2dc44dc","displayId":"adc708bb125","author":' +
            '{"name":"jcoder","emailAddress":"jcoder@code.com","id":1817,"displayName":"Joe Coder","active":true,' +
            '"slug":"jcoder","type":"NORMAL"},"authorTimestamp":1432081865000,"message":"don\'t call evaluate if user ' +
            'is null","parents":[{"id":"70a121a7e8f86c54467a43bd29066e5ff1174510","displayId":"70a121a7e8f"}]},' +
            '{"id":"70a121a7e8f86c54467a43bd29066e5ff1174510","displayId":"70a121a7e8f","author":{"name":"jcoder",' +
            '"emailAddress":"jcoder@code.com","id":1817,"displayName":"Joe Coder","active":true,"slug":"jcoder",' +
            '"type":"NORMAL"},"authorTimestamp":1432081404000,"message":"Merge branch \'my-work\' ' +
            'into master","parents":[{"id":' +
            '"3c3b942b09767e01c25e42bcb65a6630e8b2fc75","displayId":"3c3b942b097"},{"id":' +
            '"13881c94156429084910e6ca417c48fcb6d74be8","displayId":"13881c94156"}]}],"size":2,"isLastPage":false,' +
            '"start":0,"limit":2,"nextPageStart":2}'
    }
}
