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

package pl.mk5.gdx.fireapp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;
import pl.mk5.gdx.fireapp.promises.FuturePromise;
import pl.mk5.gdx.fireapp.storage.ImageHelper;

class TextureRegionDownloader {

    private final String path;
    private static final ImageHelper imageHelper = new ImageHelper();

    TextureRegionDownloader(String path) {
        this.path = path;
    }

    FuturePromise<TextureRegion> download() {
        return FuturePromise.when(new Consumer<FuturePromise<TextureRegion>>() {
            @Override
            public void accept(final FuturePromise<TextureRegion> textureRegionFuturePromise) {
                GdxFIRStorage.instance().download(path, Long.MAX_VALUE).then(new Consumer<byte[]>() {
                    @Override
                    public void accept(final byte[] bytes) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
                                    TextureRegion region = imageHelper.createTextureFromBytes(bytes);
                                    textureRegionFuturePromise.doComplete(region);
                                } else {
                                    imageHelper.createTextureFromBytes(bytes, new Consumer<TextureRegion>() {
                                        @Override
                                        public void accept(TextureRegion textureRegion) {
                                            textureRegionFuturePromise.doComplete(textureRegion);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).fail(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) {
                        textureRegionFuturePromise.doFail(s, throwable);
                    }
                });
            }
        });
    }

}
