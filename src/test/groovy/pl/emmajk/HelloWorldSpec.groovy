package pl.emmajk

import spock.lang.Specification

class HelloWorldSpec extends Specification {

    def "given no arguments when calling greeting should return the hello world message"() {
        expect:
        HelloWorld.greeting() == "Hello, World!"
    }
}
