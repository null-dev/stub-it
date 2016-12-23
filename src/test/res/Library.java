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
    private long lastLongId = Integer.MAX_VALUE;
    private List<Manga> mangas = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private Map<Long, List<Chapter>> chapters = new HashMap<>();
    private Map<Long, List<Integer>> mangaCategories = new HashMap<>();
    private Map<Long, List<Track>> tracks = new HashMap<>();
    private final AtomicReference<ReentrantLock> masterLock = new AtomicReference<>(null);

    private final String three = "one" + "two";
    private final boolean mathTest = !((byte) (1 + 2 << 5 - 10 / 2) > 20);
    private final boolean mathTest2 = !((byte) (1 + 2 << 5 - 10 / 2) > Math.abs(0)); //Should be stubbed
    private final int[] arrayTest = new int[]{0,1,2,3,4};
    private final Class classTest = HashMap.class;

    {
        System.out.println("This is not stubbed out!");
    }

    static {
        System.out.println("Static blocks not stubbed out!");
    }

    public synchronized void copyFrom(Library library) {
        synchronized (library) {
            this.lastIntId = library.lastIntId;
            this.lastLongId = library.lastLongId;
            this.mangas = new ArrayList<>(library.mangas);
            this.categories = new ArrayList<>(library.categories);
            this.chapters = new HashMap<>(library.chapters);
            this.mangaCategories = new HashMap<>(library.mangaCategories);
            this.tracks = new HashMap<>(library.tracks);
        }
    }

    public Library() {}

    public Library(Library library) {
        copyFrom(library);
    }

    public synchronized List<Manga> getMangas() {
        return mangas;
    }

    public synchronized List<Manga> getFavoriteMangas() {
        return mangas.stream().filter(Manga::getFavorite).collect(Collectors.toList());
    }

    public synchronized List<Category> getCategories() {
        return categories;
    }

    public synchronized Map<Long, List<Chapter>> getChapters() {
        return chapters;
    }

    @NotNull
    public synchronized List<Chapter> getChapters(Manga manga) {
        List<Chapter> mChapters = chapters.get(manga.getId());
        if (mChapters == null) {
            mChapters = new ArrayList<>();
        }
        return mChapters;
    }

    public synchronized Chapter getChapter(long id) {
        return OptionalUtils.getOrNull(
                chapters.values()
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(chapter -> Objects.equals(chapter.getId(), id))
                        .findFirst());
    }

    public synchronized Map<Long, List<Track>> getTracks() {
        return tracks;
    }

    public synchronized List<Track> getTracks(Manga manga) {
        List<Track> mSync = tracks.get(manga.getId());
        if (mSync == null) {
            mSync = new ArrayList<>();
        }
        return mSync;
    }

    public synchronized Map<Long, List<Integer>> getMangaCategories() {
        return mangaCategories;
    }

    public synchronized Category findCategory(String category) {
        String nameLower = category.toLowerCase();
        for(Category cat : categories) {
           if(cat.getNameLower().equals(nameLower)) {
               return cat;
           }
        }
        return null;
    }

    public synchronized List<Category> getCategoriesForManga(Manga manga) {
        List<Integer> mCategories = mangaCategories.get(manga.getId());
        if (mCategories == null) {
            mCategories = new ArrayList<>();
        }
        return mCategories
                .stream()
                .map(
                        integer -> {
                            for (Category category : categories) {
                                if (Objects.equals(category.getId(), integer)) {
                                    return category;
                                }
                            }
                            return null;
                        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public synchronized <T> boolean removeWithIdInt(
            T toFind, List<T> objects, Function<T, Integer> mapping) {
        boolean found = false;
        if (toFind != null) {
            int targetId = mapping.apply(toFind);
            Iterator<T> iterator = objects.iterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (next != null && Objects.equals(targetId, mapping.apply(next))) {
                    found = true;
                    iterator.remove();
                }
            }
        }
        return found;
    }

    public synchronized <T> boolean removeWithIdLong(
            T toFind, List<T> objects, Function<T, Long> mapping) {
        boolean found = false;
        if (toFind != null) {
            long targetId = mapping.apply(toFind);
            Iterator<T> iterator = objects.iterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (next != null && Objects.equals(targetId, mapping.apply(next))) {
                    found = true;
                    iterator.remove();
                }
            }
        }
        return found;
    }

    public synchronized int deleteChapters(List<Chapter> chaptersToDelete) {
        int removed = 0;
        for(Chapter chapterToDelete : chaptersToDelete) {
            List<Chapter> targetChapters = chapters.get(UnboxTherapy.unbox(chapterToDelete.getManga_id()));
            if (targetChapters != null) {
                if (removeWithIdLong(chapterToDelete, targetChapters, new ChapterIdMapping())) {
                    removed++;
                }
            }
        }
        return removed;
    }

    public synchronized void fixChaptersSourceOrder(List<Chapter> chaptersToFix) {
        chapters.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(
                        chapterInDb -> {
                            for (Chapter chapterToFix : chaptersToFix) {
                                if (Objects.equals(chapterToFix.getUrl(), chapterInDb.getUrl())
                                        && Objects.equals(
                                                chapterToFix.getManga_id(),
                                                chapterInDb.getManga_id())) {
                                    chapterInDb.setSource_order(chapterToFix.getSource_order());
                                }
                            }
                        });
    }

    public synchronized int insertCategory(Category category) {
        boolean removed = removeWithIdInt(category, categories, new CategoryIdMapping());
        categories.add(category);
        return removed ? UnboxTherapy.unbox(category.getId()) : newIntId();
    }

    public synchronized void deleteOldMangasCategories(List<Manga> toDelete) {
        for (Manga manga : toDelete) {
            if (manga != null) mangaCategories.remove(manga.getId());
        }
    }

    public synchronized void deleteMangaCategories(List<MangaCategory> toDelete) {
        for(MangaCategory mangaCategory : toDelete) {
            List<Integer> categories = mangaCategories.get(mangaCategory.getManga_id());
            if(categories != null)
                categories.remove(mangaCategory.getCategory_id());
        }
    }

    public synchronized void insertMangasCategories(List<MangaCategory> toInsert) {
        for (MangaCategory mangaCategory : toInsert) {
            List<Integer> categories = mangaCategories.computeIfAbsent(mangaCategory.getManga_id(), k -> new ArrayList<>());
            categories.add(mangaCategory.getCategory_id());
        }
    }

    public synchronized long insertManga(Manga manga) {
        boolean removed = removeWithIdLong(manga, mangas, new MangaIdMapping());
        mangas.add(manga);
        return removed ? UnboxTherapy.unbox(manga.getId()) : newLongId();
    }

    public synchronized int insertChapters(List<Chapter> toInsert) {
        int inserted = 0;
        for (Chapter chapter1 : toInsert) {
            LongInsertionResult result = insertChapter(chapter1);
            chapter1.setId(result.getNewId());
            if(result.wasInserted()) {
                inserted++;
            }
        }
        return inserted;
    }

    public synchronized LongInsertionResult insertChapter(Chapter chapter) {
        List<Chapter> found = chapters.computeIfAbsent(chapter.getManga_id(), k -> new ArrayList<>());
        boolean removed = removeWithIdLong(chapter, found, new ChapterIdMapping());
        found.add(chapter);
        return new LongInsertionResult(!removed, removed ? UnboxTherapy.unbox(chapter.getId()) : newLongId());
    }

    public synchronized void insertTracks(List<Track> tracks) {
        for (Track track : tracks) {
            track.setId(insertTrack(track));
        }
    }

    public synchronized long insertTrack(Track track) {
        List<Track> found = tracks.computeIfAbsent(track.getManga_id(), k -> new ArrayList<>());
        boolean removed = removeWithIdLong(track, found, new TrackIdMapping());
        found.add(track);
        return removed ? UnboxTherapy.unbox(track.getId()) : newLongId();
    }

    public synchronized Manga getManga(String url, int source) {
        return OptionalUtils.getOrNull(
                mangas.stream()
                        .filter(manga -> Objects.equals(manga.getUrl(), url))
                        .filter(manga -> manga.getSource() == source)
                        .findFirst());
    }

    public synchronized Manga getManga(long id) {
        return OptionalUtils.getOrNull(
                mangas.stream().filter(manga -> Objects.equals(manga.getId(), id)).findFirst());
    }

    public synchronized void deleteCategories(List<String> deleteCategories) {
        List<String> lowercaseCategories = deleteCategories.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        categories.removeIf(category -> lowercaseCategories.contains(category.getNameLower()));
    }

    public synchronized void deleteCategory(String category) {
        categories.removeIf(category1 -> category1.getNameLower().equals(category.toLowerCase()));
    }

    public AtomicReference<ReentrantLock> getMasterLock() {
        return masterLock;
    }

    public int newIntId() {
        return lastIntId++;
    }

    public long newLongId() {
        return lastLongId++;
    }

    public int getLastIntId() {
        return lastIntId;
    }

    public void setLastIntId(int lastIntId) {
        this.lastIntId = lastIntId;
    }

    public long getLastLongId() {
        return lastLongId;
    }

    public void setLastLongId(long lastLongId) {
        this.lastLongId = lastLongId;
    }

    public LibraryTransaction newTransaction() {
        return new LibraryTransaction();
    }

    public class LibraryTransaction {
        Library library = new Library(Library.this);

        public Library getLibrary() {
            return library;
        }

        public void apply() {
            Library.this.copyFrom(library);
        }
    }

    private class CategoryIdMapping implements Function<Category, Integer> {
        @Override
        public Integer apply(Category category) {
            return UnboxTherapy.unbox(category.getId());
        }

    }
    private class MangaIdMapping implements Function<Manga, Long> {
        @Override
        public Long apply(Manga manga) {
            return UnboxTherapy.unbox(manga.getId());
        }

    }
    private class ChapterIdMapping implements Function<Chapter, Long> {
        @Override
        public Long apply(Chapter chapter) {
            return UnboxTherapy.unbox(chapter.getId());
        }

    }
    private class TrackIdMapping implements Function<Track, Long> {
        @Override
        public Long apply(Track track) {
            return UnboxTherapy.unbox(track.getId());
        }

    }

    public class IntInsertionResult extends InsertionResult {
        private final int newId;

        public IntInsertionResult(boolean inserted, int newId) {
            super(inserted);
            this.newId = newId;
        }

        public int getNewId() {
            return newId;
        }
    }

    public class LongInsertionResult extends InsertionResult {
        private final long newId;

        public LongInsertionResult(boolean inserted, long newId) {
            super(inserted);
            this.newId = newId;
        }

        public long getNewId() {
            return newId;
        }
    }

    public class InsertionResult {
        private final boolean inserted;

        public InsertionResult(boolean inserted) {
            this.inserted = inserted;
        }

        public boolean wasInserted() {
            return inserted;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Library library = (Library) o;

        if (lastIntId != library.lastIntId) return false;
        if (lastLongId != library.lastLongId) return false;
        if (mangas != null ? !mangas.equals(library.mangas) : library.mangas != null) return false;
        if (categories != null
                ? !categories.equals(library.categories)
                : library.categories != null) return false;
        if (chapters != null ? !chapters.equals(library.chapters) : library.chapters != null)
            return false;
        if (mangaCategories != null
                ? !mangaCategories.equals(library.mangaCategories)
                : library.mangaCategories != null) return false;
        if (tracks != null
                ? !tracks.equals(library.tracks)
                : library.tracks != null) return false;
        return masterLock != null
                ? masterLock.equals(library.masterLock)
                : library.masterLock == null;
    }

    @Override
    public int hashCode() {
        int result = lastIntId;
        result = 31 * result + (int) (lastLongId ^ (lastLongId >>> 32));
        result = 31 * result + (mangas != null ? mangas.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        result = 31 * result + (chapters != null ? chapters.hashCode() : 0);
        result = 31 * result + (mangaCategories != null ? mangaCategories.hashCode() : 0);
        result = 31 * result + (tracks != null ? tracks.hashCode() : 0);
        result = 31 * result + (masterLock != null ? masterLock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Library{"
                + "lastIntId="
                + lastIntId
                + ", lastLongId="
                + lastLongId
                + ", mangas="
                + mangas
                + ", categories="
                + categories
                + ", chapters="
                + chapters
                + ", mangaCategories="
                + mangaCategories
                + ", tracks="
                + tracks
                + ", masterLock="
                + masterLock
                + '}';
    }
}
