# Create your views here.

from django.http import HttpResponse
from recommend.details import Details
from django.shortcuts import render_to_response

def index(request):
    return HttpResponse("Hello, world. You're at the recommend index.")

def results(request, user_id):
    return HttpResponse("You're looking at results of USERID %s." % user_id)

def books(request, user_id, friend_id):

    d = Details(user_id,friend_id)
    common_books = d.getCommonInterests("books")

    return render_to_response('recommend/books.html', {'common_books': common_books})

def music(request, user_id, friend_id):
    
    d = Details(user_id,friend_id)
    common_music = d.getCommonInterests("music")

    return render_to_response('recommend/music.html', {'common_music': common_music})

def movies(request, user_id, friend_id):

    d = Details(user_id,friend_id)
    common_movies = d.getCommonInterests("movies")

    return render_to_response('recommend/movies.html', {'common_movies': common_movies})

def groups(request, user_id, friend_id):

    d = Details(user_id,friend_id)
    common_groups = d.getCommonInterests("groups")

    return render_to_response('recommend/groups.html', {'common_groups': common_groups})

def television(request, user_id, friend_id):

    d = Details(user_id,friend_id)
    common_televisions = d.getCommonInterests("television")

    return render_to_response('recommend/television.html', {'common_televisions': common_televisions})

