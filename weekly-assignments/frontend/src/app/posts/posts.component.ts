import { Component, OnInit } from '@angular/core';
import { PostsService } from './services/posts.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  posts: any;

  constructor(private postsService: PostsService) {}

  ngOnInit() {
    // get all the posts from public api
    this.postsService.getPosts().subscribe((response) => {
      this.posts = response;
    });
  }
}
