/*
 * Copyright (c) 2012, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package websocket.chat;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.runtime.context.scope.ThreadLocal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

/**
 * Defines bean factories for polyglot {@link Context} and {@link Engine}
 * so that they can be injected into other beans. All {@link Context}
 * instances share one {@link Engine}, which means that they will share
 * optimized code, but otherwise they will be independent of each other.
 */
@Factory
@ConfigurationProperties("scripts")
public class PolyglotContextFactories {
    String pythonVenv;

    @Singleton
    @Bean(preDestroy = "close")
    Engine createEngine() {
        return Engine.newBuilder().allowExperimentalOptions(true).build();
    }

    @Prototype
    @Bean(preDestroy = "close")
    Context createContext(Engine engine, ResourceResolver resolver) throws URISyntaxException {
        Path exe = Paths.get(resolver.getResource(pythonVenv).get().toURI()).resolveSibling("bin").resolve("exe");
        return Context.newBuilder("python", "R")
                .option("python.PosixModuleBackend", "native")
                .option("python.ForceImportSite", "true")
                .option("python.Executable", exe.toString())
                .allowAllAccess(true)
                .engine(engine)
                .build();
    }
}
