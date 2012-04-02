from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^recommend/$', 'recommend.views.index'),
    url(r'^recommend/results/(?P<user_id>\d+)/$', 'recommend.views.results'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/$', 'recommend.views.books'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/books$', 'recommend.views.books'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/music$', 'recommend.views.music'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/movies$', 'recommend.views.movies'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/groups$', 'recommend.views.groups'),
    url(r'^recommend/results/(?P<user_id>\d+)/details/(?P<friend_id>\d+)/television$', 'recommend.views.television'),
    url(r'^admin/', include(admin.site.urls)),
)

from FARS import settings
if settings.DEBUG:
    urlpatterns += patterns('',
        (r'^mymedia/(?P<path>.*)$', 'django.views.static.serve',  
         {'document_root':     settings.MEDIA_ROOT}),
    )
