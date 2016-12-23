/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.nulldev.ts.library;

import eu.kanade.tachiyomi.data.database.models.*;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import xyz.nulldev.ts.util.OptionalUtils;
import xyz.nulldev.ts.util.UnboxTherapy;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 *
 * (Mostly) Drop-in replacement for DatabaseHelper
 *
 * TODO I honestly see no point to this class anymore so it will be eventually replaced with the SQL db again.
 */
public class Library {

    private int lastIntId = 10;

    private long lastLongId = 0;

    private List<Manga> mangas = null;

    private List<Category> categories = null;

    private Map<Long, List<Chapter>> chapters = null;

    private Map<Long, List<Integer>> mangaCategories = null;

    private Map<Long, List<Track>> tracks = null;

    private final AtomicReference<ReentrantLock> masterLock = null;

    private final String three = "one" + "two";

    private final boolean mathTest = !((byte) (1 + 2 << 5 - 10 / 2) > 20);

    //Should be stubbed
    private final boolean mathTest2 = false;

    private final int[] arrayTest = new int[] { 0, 1, 2, 3, 4 };

    private final Class classTest = HashMap.class;

    {
    }

    static {
    }

    public synchronized void copyFrom(Library library) {
        throw new RuntimeException("Stub!");
    }

    public Library() {
        throw new RuntimeException("Stub!");
    }

    public Library(Library library) {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<Manga> getMangas() {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<Manga> getFavoriteMangas() {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<Category> getCategories() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Map<Long, List<Chapter>> getChapters() {
        throw new RuntimeException("Stub!");
    }

    @NotNull
    public synchronized List<Chapter> getChapters(Manga manga) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Chapter getChapter(long id) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Map<Long, List<Track>> getTracks() {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<Track> getTracks(Manga manga) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Map<Long, List<Integer>> getMangaCategories() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Category findCategory(String category) {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<Category> getCategoriesForManga(Manga manga) {
        throw new RuntimeException("Stub!");
    }

    public synchronized <T> boolean removeWithIdInt(T toFind, List<T> objects, Function<T, Integer> mapping) {
        throw new RuntimeException("Stub!");
    }

    public synchronized <T> boolean removeWithIdLong(T toFind, List<T> objects, Function<T, Long> mapping) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int deleteChapters(List<Chapter> chaptersToDelete) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void fixChaptersSourceOrder(List<Chapter> chaptersToFix) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int insertCategory(Category category) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void deleteOldMangasCategories(List<Manga> toDelete) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void deleteMangaCategories(List<MangaCategory> toDelete) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void insertMangasCategories(List<MangaCategory> toInsert) {
        throw new RuntimeException("Stub!");
    }

    public synchronized long insertManga(Manga manga) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int insertChapters(List<Chapter> toInsert) {
        throw new RuntimeException("Stub!");
    }

    public synchronized LongInsertionResult insertChapter(Chapter chapter) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void insertTracks(List<Track> tracks) {
        throw new RuntimeException("Stub!");
    }

    public synchronized long insertTrack(Track track) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Manga getManga(String url, int source) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Manga getManga(long id) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void deleteCategories(List<String> deleteCategories) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void deleteCategory(String category) {
        throw new RuntimeException("Stub!");
    }

    public AtomicReference<ReentrantLock> getMasterLock() {
        throw new RuntimeException("Stub!");
    }

    public int newIntId() {
        throw new RuntimeException("Stub!");
    }

    public long newLongId() {
        throw new RuntimeException("Stub!");
    }

    public int getLastIntId() {
        throw new RuntimeException("Stub!");
    }

    public void setLastIntId(int lastIntId) {
        throw new RuntimeException("Stub!");
    }

    public long getLastLongId() {
        throw new RuntimeException("Stub!");
    }

    public void setLastLongId(long lastLongId) {
        throw new RuntimeException("Stub!");
    }

    public LibraryTransaction newTransaction() {
        throw new RuntimeException("Stub!");
    }

    public class LibraryTransaction {

        Library library = null;

        public Library getLibrary() {
            throw new RuntimeException("Stub!");
        }

        public void apply() {
            throw new RuntimeException("Stub!");
        }
    }

    private class CategoryIdMapping implements Function<Category, Integer> {

        @Override
        public Integer apply(Category category) {
            throw new RuntimeException("Stub!");
        }
    }

    private class MangaIdMapping implements Function<Manga, Long> {

        @Override
        public Long apply(Manga manga) {
            throw new RuntimeException("Stub!");
        }
    }

    private class ChapterIdMapping implements Function<Chapter, Long> {

        @Override
        public Long apply(Chapter chapter) {
            throw new RuntimeException("Stub!");
        }
    }

    private class TrackIdMapping implements Function<Track, Long> {

        @Override
        public Long apply(Track track) {
            throw new RuntimeException("Stub!");
        }
    }

    public class IntInsertionResult extends InsertionResult {

        private final int newId;

        public IntInsertionResult(boolean inserted, int newId) {
            throw new RuntimeException("Stub!");
        }

        public int getNewId() {
            throw new RuntimeException("Stub!");
        }
    }

    public class LongInsertionResult extends InsertionResult {

        private final long newId;

        public LongInsertionResult(boolean inserted, long newId) {
            throw new RuntimeException("Stub!");
        }

        public long getNewId() {
            throw new RuntimeException("Stub!");
        }
    }

    public class InsertionResult {

        private final boolean inserted;

        public InsertionResult(boolean inserted) {
            throw new RuntimeException("Stub!");
        }

        public boolean wasInserted() {
            throw new RuntimeException("Stub!");
        }
    }

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}
