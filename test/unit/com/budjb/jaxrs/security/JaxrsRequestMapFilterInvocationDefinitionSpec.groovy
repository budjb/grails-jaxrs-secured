package com.budjb.jaxrs.security

import com.budjb.jaxrs.security.test.JaxrsFilterInvocationTest
import grails.plugin.springsecurity.InterceptedUrl
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.security.access.ConfigAttribute
import spock.lang.Unroll

class JaxrsRequestMapFilterInvocationDefinitionSpec extends JaxrsFilterInvocationTest {
    JaxrsRequestMapFilterInvocationDefinition jaxrsRequestMapFilterInvocationDefinition

    def setup() {
        jaxrsRequestMapFilterInvocationDefinition = new TestRequestMapFilterInvocationDefinition()
        jaxrsRequestMapFilterInvocationDefinition.rejectIfNoRule = true
        jaxrsRequestMapFilterInvocationDefinition.allow404 = true
        jaxrsRequestMapFilterInvocationDefinition.initialize([classSecurityResourceGrailsClass, resourceSecurityResourceGrailsClass] as GrailsClass[])
    }

    @Unroll
    def 'Using the class security resource, ensure that "#method #path" returns security rule "#result"'() {
        setup:
        httpServletRequest.getRequestURI() >> path
        httpServletRequest.getContextPath() >> 'https://example.com'
        httpServletRequest.getMethod() >> method

        when:
        Collection<ConfigAttribute> results = jaxrsRequestMapFilterInvocationDefinition.getAttributes(filterInvocation)

        then:
        if (result == null) {
            assert results == null
        }
        else {
            assert results.size() == 1
            assert results[0].attribute == result
        }

        where:
        path                                               | method || result
        'https://example.com/api/class_security'           | 'GET'  || 'ROLE_USER'
        'https://example.com/api/class_security/anonymous' | 'GET'  || 'IS_AUTHENTICATED_ANONYMOUSLY'
        'https://example.com/api/class_security/inherit'   | 'GET'  || 'ROLE_READONLY'
    }

    @Unroll
    def 'Using the resource security resource, ensure that "#method #path" returns security rule "#result"'() {
        setup:
        httpServletRequest.getRequestURI() >> path
        httpServletRequest.getContextPath() >> 'https://example.com'
        httpServletRequest.getMethod() >> method

        when:
        Collection<ConfigAttribute> results = jaxrsRequestMapFilterInvocationDefinition.getAttributes(filterInvocation)

        then:
        if (result == null) {
            assert results == null
        }
        else {
            assert results.size() == 1
            assert results[0].attribute == result
        }

        where:
        path                                                  | method || result
        'https://example.com/api/resource_security'           | 'GET'  || 'ROLE_USER'
        'https://example.com/api/resource_security/anonymous' | 'GET'  || 'IS_AUTHENTICATED_ANONYMOUSLY'
        'https://example.com/api/resource_security/norule'    | 'GET'  || '_DENY_'
    }

    @Unroll
    def 'When rejectIfNoRule == #rejectIfNoRule and allow404 == #allow404, the security rule for an undefined resource is "#rule"'() {
        setup:
        httpServletRequest.getRequestURI() >> 'https://example.com/api/undefined_resource'
        httpServletRequest.getContextPath() >> 'https://example.com'
        httpServletRequest.getMethod() >> 'GET'

        jaxrsRequestMapFilterInvocationDefinition.rejectIfNoRule = rejectIfNoRule
        jaxrsRequestMapFilterInvocationDefinition.allow404 = allow404

        when:
        Collection<ConfigAttribute> results = jaxrsRequestMapFilterInvocationDefinition.getAttributes(filterInvocation)

        then:
        if (rule == null) {
            assert results == null
        }
        else {
            assert results.size() == 1
            assert results[0].attribute == rule
        }

        where:
        rejectIfNoRule | allow404 || rule
        false          | false    || null
        true           | false    || '_DENY_'
        false          | true     || null
        true           | true     || 'IS_AUTHENTICATED_ANONYMOUSLY'
    }

    class TestRequestMapFilterInvocationDefinition extends JaxrsRequestMapFilterInvocationDefinition {
        protected List<InterceptedUrl> loadRequestMaps() {
            return [
                new InterceptedUrl('/api/class_security', ['ROLE_USER'], null),
                new InterceptedUrl('/api/class_security/anonymous', ['IS_AUTHENTICATED_ANONYMOUSLY'], null),
                new InterceptedUrl('/api/class_security/**', ['ROLE_READONLY'], null),

                new InterceptedUrl('/api/resource_security', ['ROLE_USER'], null),
                new InterceptedUrl('/api/resource_security/anonymous', ['IS_AUTHENTICATED_ANONYMOUSLY'], null)
            ]
        }
    }
}
