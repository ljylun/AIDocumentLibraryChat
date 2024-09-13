/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.aidoclibchat.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.xxx.aidoclibchat.domain.model.entity.Book;
import ch.xxx.aidoclibchat.domain.model.entity.BookRepository;

@Repository
public class BookRepositoryBean implements BookRepository {
	private JpaBookRepository jpaBookRepository;
	
	public BookRepositoryBean(JpaBookRepository jpaBookRepository) {
		this.jpaBookRepository = jpaBookRepository;
	}
	
	public List<Book> findByTitleWithChapters(String title) {
		return this.jpaBookRepository.findByTitleWitChapters(title);
	}
	
	public Book save(Book book) {
		return this.jpaBookRepository.save(book);
	}

	@Override
	public Optional<Book> findById(UUID uuid) {
		return this.jpaBookRepository.findById(uuid);
	}
}
