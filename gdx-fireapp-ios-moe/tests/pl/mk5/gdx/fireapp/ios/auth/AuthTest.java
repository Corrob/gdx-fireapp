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

package pl.mk5.gdx.fireapp.ios.auth;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.ptr.Ptr;
import org.moe.natj.general.ptr.impl.PtrFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apple.foundation.NSError;
import apple.foundation.NSURL;
import bindings.google.firebaseauth.FIRAuth;
import bindings.google.firebaseauth.FIRUser;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;
import pl.mk5.gdx.fireapp.ios.GdxIOSAppTest;

@PrepareForTest({NatJ.class, FIRAuth.class, FIRUser.class, NSURL.class, NSError.class, PtrFactory.class, Ptr.class})
public class AuthTest extends GdxIOSAppTest {

    private FIRAuth firAuth;
    private Consumer consumer;
    private BiConsumer biConsumer;

    @Override
    public void setup() {
        super.setup();
        PowerMockito.mockStatic(FIRAuth.class);
        PowerMockito.mockStatic(FIRUser.class);
        PowerMockito.mockStatic(PtrFactory.class);
        PowerMockito.mockStatic(NSError.class);
        PowerMockito.mockStatic(Ptr.class);
        PowerMockito.mockStatic(NSURL.class);
        firAuth = PowerMockito.mock(FIRAuth.class);
        Mockito.when(FIRAuth.auth()).thenReturn(firAuth);
        consumer = Mockito.mock(Consumer.class);
        biConsumer = Mockito.mock(BiConsumer.class);
    }

    @Test
    public void getCurrentUser() {
        // Given
        FIRUser firUser = Mockito.mock(FIRUser.class);
        NSURL nsurl = Mockito.mock(NSURL.class);
        Mockito.when(nsurl.path()).thenReturn("test");
        Mockito.when(firAuth.currentUser()).thenReturn(firUser);
        Mockito.when(firUser.displayName()).thenReturn("display_name");
        Mockito.when(firUser.email()).thenReturn("email");
        Mockito.when(firUser.photoURL()).thenReturn(nsurl);
        Mockito.when(firUser.uid()).thenReturn("uid");
        Mockito.when(firUser.isAnonymous()).thenReturn(true);
        Mockito.when(firUser.isEmailVerified()).thenReturn(false);
        Auth auth = new Auth();

        // When
        GdxFirebaseUser gdxFirebaseUser = auth.getCurrentUser();

        // Then
        Assert.assertNotNull(gdxFirebaseUser);
        Assert.assertNotNull(gdxFirebaseUser.getUserInfo());
        Assert.assertEquals("display_name", gdxFirebaseUser.getUserInfo().getDisplayName());
        Assert.assertEquals("email", gdxFirebaseUser.getUserInfo().getEmail());
        Assert.assertEquals("test", gdxFirebaseUser.getUserInfo().getPhotoUrl());
        Assert.assertEquals("uid", gdxFirebaseUser.getUserInfo().getUid());
        Assert.assertEquals(true, gdxFirebaseUser.getUserInfo().isAnonymous());
        Assert.assertEquals(false, gdxFirebaseUser.getUserInfo().isEmailVerified());
    }

    @Test
    public void createUserWithEmailAndPassword() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_createUserWithEmailPasswordCompletion) invocation.getArgument(2)).call_createUserWithEmailPasswordCompletion(null, null);
                return null;
            }
        }).when(firAuth).createUserWithEmailPasswordCompletion(Mockito.anyString(), Mockito.any(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.createUserWithEmailAndPassword("email", "password".toCharArray())
                .subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).createUserWithEmailPasswordCompletion(Mockito.eq("email"), Mockito.eq("password"), Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(GdxFirebaseUser.class));
    }

    @Test
    public void createUserWithEmailAndPassword_fail() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_createUserWithEmailPasswordCompletion) invocation.getArgument(2)).call_createUserWithEmailPasswordCompletion(null, Mockito.mock(NSError.class));
                return null;
            }
        }).when(firAuth).createUserWithEmailPasswordCompletion(Mockito.anyString(), Mockito.any(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.createUserWithEmailAndPassword("email", "password".toCharArray()).fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).createUserWithEmailPasswordCompletion(Mockito.eq("email"), Mockito.eq("password"), Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.nullable(Exception.class));
    }

    @Test
    public void signInWithEmailAndPassword() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInWithEmailPasswordCompletion) invocation.getArgument(2)).call_signInWithEmailPasswordCompletion(null, null);
                return null;
            }
        }).when(firAuth).signInWithEmailPasswordCompletion(Mockito.anyString(), Mockito.any(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInWithEmailAndPassword("email", "password".toCharArray()).subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInWithEmailPasswordCompletion(Mockito.eq("email"), Mockito.eq("password"), Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(GdxFirebaseUser.class));
    }

    @Test
    public void signInWithEmailAndPassword_fail() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInWithEmailPasswordCompletion) invocation.getArgument(2)).call_signInWithEmailPasswordCompletion(null, Mockito.mock(NSError.class));
                return null;
            }
        }).when(firAuth).signInWithEmailPasswordCompletion(Mockito.anyString(), Mockito.any(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInWithEmailAndPassword("email", "password".toCharArray()).fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInWithEmailPasswordCompletion(Mockito.eq("email"), Mockito.eq("password"), Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.any(Exception.class));
    }

    @Test
    public void signInWithToken() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInWithCustomTokenCompletion) invocation.getArgument(1)).call_signInWithCustomTokenCompletion(null, null);
                return null;
            }
        }).when(firAuth).signInWithCustomTokenCompletion(Mockito.anyString(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInWithToken("token").subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInWithCustomTokenCompletion(Mockito.eq("token"), Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(GdxFirebaseUser.class));
    }

    @Test
    public void signInWithToken_fail() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInWithCustomTokenCompletion) invocation.getArgument(1)).call_signInWithCustomTokenCompletion(null, Mockito.mock(NSError.class));
                return null;
            }
        }).when(firAuth).signInWithCustomTokenCompletion(Mockito.anyString(), Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInWithToken("token").fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInWithCustomTokenCompletion(Mockito.eq("token"), Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.any(Exception.class));
    }

    @Test
    public void signInAnonymously() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInAnonymouslyWithCompletion) invocation.getArgument(0)).call_signInAnonymouslyWithCompletion(null, null);
                return null;
            }
        }).when(firAuth).signInAnonymouslyWithCompletion(Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInAnonymously().subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInAnonymouslyWithCompletion(Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(GdxFirebaseUser.class));
    }

    @Test
    public void signInAnonymously_fail() {
        // Given
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_signInAnonymouslyWithCompletion) invocation.getArgument(0)).call_signInAnonymouslyWithCompletion(null, Mockito.mock(NSError.class));
                return null;
            }
        }).when(firAuth).signInAnonymouslyWithCompletion(Mockito.any());
        Auth auth = new Auth();

        // When
        auth.signInAnonymously().fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signInAnonymouslyWithCompletion(Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.any(Exception.class));
    }

    @Test
    public void signOut() {
        // Given
        PowerMockito.mockStatic(PtrFactory.class);
        Mockito.when(PtrFactory.newObjectReference(NSError.class)).thenReturn(Mockito.mock(Ptr.class));
        Auth auth = new Auth();

        // When
        auth.signOut().subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signOut(Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.any());
    }

    @Test
    public void signOut_fail() {
        // Given
        Ptr<NSError> ptrError = PowerMockito.mock(Ptr.class);
        NSError nsError = Mockito.mock(NSError.class);
        Mockito.when(PtrFactory.newObjectReference(NSError.class)).thenReturn(ptrError);
        Mockito.when(ptrError.get()).thenReturn(nsError);
        Auth auth = new Auth();

        // When
        auth.signOut().fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).signOut(Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.any(Exception.class));
    }

    @Test
    public void sendPasswordResetEmail() {
        // Given
        PowerMockito.mockStatic(PtrFactory.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_sendPasswordResetWithEmailCompletion) invocation.getArgument(1))
                        .call_sendPasswordResetWithEmailCompletion(null);
                return null;
            }
        }).when(firAuth).sendPasswordResetWithEmailCompletion(Mockito.anyString(), Mockito.any());
        Auth auth = new Auth();
        String arg1 = "email";

        // When
        auth.sendPasswordResetEmail(arg1).subscribe(consumer);

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).sendPasswordResetWithEmailCompletion(Mockito.eq(arg1), Mockito.any());
        Mockito.verify(consumer, VerificationModeFactory.times(1)).accept(Mockito.any());
    }

    @Test
    public void sendPasswordResetEmail_fail() {
        // Given
        PowerMockito.mockStatic(PtrFactory.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ((FIRAuth.Block_sendPasswordResetWithEmailCompletion) invocation.getArgument(1)).call_sendPasswordResetWithEmailCompletion(Mockito.mock(NSError.class));
                return null;
            }
        }).when(firAuth).sendPasswordResetWithEmailCompletion(Mockito.anyString(), Mockito.any());
        Auth auth = new Auth();
        String arg1 = "email";

        // When
        auth.sendPasswordResetEmail(arg1).fail(biConsumer).subscribe();

        // Then
        Mockito.verify(firAuth, VerificationModeFactory.times(1)).sendPasswordResetWithEmailCompletion(Mockito.eq(arg1), Mockito.any());
        Mockito.verify(biConsumer, VerificationModeFactory.times(1)).accept(Mockito.nullable(String.class), Mockito.any(Exception.class));
    }

}