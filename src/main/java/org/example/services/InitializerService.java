package org.example.services;

import com.github.javafaker.Faker;
import org.example.entities.CategoryEntity;
import org.example.entities.PostEntity;
import org.example.entities.PostTagEntity;
import org.example.entities.TagEntity;
import org.example.repositories.CategoryRepository;
import org.example.repositories.PostRepository;
import org.example.repositories.PostTagRepository;
import org.example.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InitializerService {
    private final Faker faker;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;

    private Map<String,String> letters = new HashMap<>();

    public InitializerService(CategoryRepository categoryRepository, TagRepository tagRepository,
                              PostRepository postRepository, PostTagRepository postTagRepository) {
        faker = new Faker(new Locale("uk"));
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.postTagRepository = postTagRepository;
        fillLetters();
    }

    public void seedCategories(){
        final int count = 10;
        if(categoryRepository.count()==0){
            for(int i = 0;i<count;i++){
                CategoryEntity category = new CategoryEntity();
                String name = faker.commerce().department();
                category.setName(name);
                String slug = DoSlugUrl(name);
                category.setUrlSlug(slug);
                String description = "Виробник : "+faker.company().name()+
                        " | Матеріал : "+ faker.commerce().material();
                category.setDescription(description);
                categoryRepository.save(category);
            }
        }
    }

    public void seedTags(){
        final int count = 10;
        if(tagRepository.count()<count){
            for(int i = 0;i<count;i++){
                TagEntity tag = new TagEntity();
                String name = faker.hacker().noun();
                tag.setName(name);
                String description = faker.hacker().adjective() + " " + faker.hacker().noun();
                tag.setDescription(description);
                String slug = "tag "+name+" "+description;
                slug = String.join("-",slug.split(" "));
                tag.setUrlSlug(slug);
                tagRepository.save(tag);
            }
        }
    }

    public void seedPosts(){
        final int count = 10;
        List<CategoryEntity> categories = categoryRepository.findAll();
        if(postRepository.count()<count){
            for(int i = 0;i<count;i++){
                PostEntity post = new PostEntity();

                String title = faker.book().title();
                post.setTitle(title);

                String short_description = faker.lorem().sentence();
                post.setShortDescription(short_description);

                String description=faker.lorem().paragraph();
                post.setDescription(description);

                String meta=faker.lorem().paragraph();
                post.setMeta(meta);

                String slug = "title "+title;
                slug = String.join("-",slug.split(" "));
                post.setUrlSlug(slug);
                boolean published = faker.random().nextBoolean();
                post.setPublished(published);
                post.setPostedOn(LocalDateTime.now());
                post.setCategory(categories.get(faker.random().nextInt(categories.size())));
                postRepository.save(post);
            }
        }
    }

    public void generatePostTags(int count) {
        var posts = postRepository.findAll();
        var tags = tagRepository.findAll();
        for (int i = 0; i < count; i++) {
            PostTagEntity postTag = new PostTagEntity();
            postTag.setTag(tags.get(faker.random().nextInt(tags.size())));
            postTag.setPost(posts.get(faker.random().nextInt(posts.size())));
            postTagRepository.save(postTag);
        }
    }

    public String DoSlugUrl(String text){
        String slug ="";
        for (var item : text.toCharArray()){
            if(Character.isUpperCase(item)){
                item=Character.toLowerCase(item);
            }
            String symb = letters.get(Character.toString(item));
            if(Character.isUpperCase(item)){
                symb=symb.toUpperCase();
            }
            if(symb!=null)
                slug+=symb;
        }
        return slug;
    }
    private void fillLetters(){
        letters.put(  "а","a" );
        letters.put(  "б","b" );
        letters.put(  "в","v" );
        letters.put(  "г","g" );
        letters.put(  "ґ","g" );
        letters.put(  "д","d" );
        letters.put(  "е","e" );
        letters.put( "є" ,"ye");
        letters.put( "ж" ,"zh");
        letters.put(  "з","z" );
        letters.put(  "и","u" );
        letters.put( "і" ,"i");
        letters.put( "ї" ,"yi");
        letters.put(  "й","y" );
        letters.put(  "к","k" );
        letters.put(  "л","l" );
        letters.put(  "м","m" );
        letters.put(  "н","n" );
        letters.put(  "о","o" );
        letters.put(  "п","p" );
        letters.put(  "р","r" );
        letters.put(  "с","s" );
        letters.put(  "т","t" );
        letters.put(  "у","u" );
        letters.put(  "ф","f" );
        letters.put(  "х","h" );
        letters.put(  "ц","c" );
        letters.put( "ч" ,"ch");
        letters.put( "ш" ,"sh");
        letters.put("щ" , "sch");
        letters.put( "ю", "yu");
        letters.put( "я", "ya");
        letters.put( "-","-" );
        letters.put(" " ,"-" );
        letters.put("&" ,"ta" );
    }
}