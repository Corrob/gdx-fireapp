/*
 * Copyright 2018 mk
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

package pl.mk5.gdx.fireapp.html.auth;

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.distributions.GoogleAuthDistribution;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;
import pl.mk5.gdx.fireapp.html.firebase.ScriptRunner;
import pl.mk5.gdx.fireapp.promises.FuturePromise;
import pl.mk5.gdx.fireapp.promises.Promise;

/**
 * @see GoogleAuthDistribution
 */
public class GoogleAuth implements GoogleAuthDistribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<GdxFirebaseUser> signIn() {
        return FuturePromise.when(new Consumer<FuturePromise<GdxFirebaseUser>>() {
            @Override
            public void accept(final FuturePromise<GdxFirebaseUser> gdxFirebaseUserFuturePromise) {
                ScriptRunner.firebaseScript(new Runnable() {
                    @Override
                    public void run() {
                        GoogleAuthJS.signIn(gdxFirebaseUserFuturePromise);
                    }
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<Void> signOut() {
        return FuturePromise.when(new Consumer<FuturePromise<Void>>() {
            @Override
            public void accept(final FuturePromise<Void> voidFuturePromise) {
                GdxFIRAuth.instance().signOut().then(new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        voidFuturePromise.doComplete(null);
                    }
                }).fail(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) {
                        voidFuturePromise.doFail(throwable);
                    }
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * For gwt is the same as {@link #signOut()}.
     */
    @Override
    public Promise<Void> revokeAccess() {
        return FuturePromise.when(new Consumer<FuturePromise<Void>>() {
            @Override
            public void accept(final FuturePromise<Void> voidFuturePromise) {
                signOut().then(new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        voidFuturePromise.doComplete(null);
                    }
                }).fail(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) {
                        voidFuturePromise.doFail(s, throwable);
                    }
                });
            }
        });
    }
}
