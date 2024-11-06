package io.jcervelin.services;

import org.apache.commons.lang3.RandomStringUtils

class MockOpenAIClient : AIClient {

    override suspend fun getRudeResponse(userMessage: String): String {
        return ((RandomStringUtils.randomAlphabetic(4)
                + " " + userMessage) + " ") + RandomStringUtils.randomAlphabetic(4)
    }
}
