import webapp2
from google.appengine.ext import ndb
import db_defs
from time import sleep
import datetime
import string

app = webapp2.WSGIApplication([
    ('/', 'login.Login'),
], debug=True)
app.router.add(webapp2.Route('/login', 'login.Login'))
app.router.add(webapp2.Route('/note', 'note.Note'))
app.router.add(webapp2.Route(r'/addNote', 'note.AddNote'))
app.router.add(webapp2.Route(r'/editNote', 'note.EditNote'))