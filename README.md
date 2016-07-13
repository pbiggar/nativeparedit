# native-paredit

I am a atom plugin written in clojurescript! Pretty nifty, huh? 😄

## Development

If you want to export a function to atom for use with atom commands, make sure to add them to `dev/build.clj` like so:
```
:foo 'native-paredit.core/foo
:bar 'native-paredit.core/bar
```

## Compiling and running

To compile me with a self-reloading loop, use:

```
lein run -m build/dev-repl
```

To compile me with a self-compiling loop but without live-reload:
```
lein run -m build/dev
```

To compile me for release (`:simple` optimizations), use
```
lein run -m build/release
```

After you have done that, go into the `plugin/` folder and run
```
apm link
```

native-paredit should now be installed inside atom!
