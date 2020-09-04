While Auf REST is not a Java module yet, the intention is to make it so as soon as I can figure out how to run the unit tests without an issue. Currently I'm having trouble to run unit tests by Maven because of un-accessible classes. If you can provide help, please let me know. It'd be much appreciated.

Only public classes in the following package are considered exported:
```
me.ehp246.aufrest.api
```

All code outside of this package is private. They will not be available after Auf REST is turned into a module.
