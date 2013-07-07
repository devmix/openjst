# [OpenJST](https://github.com/devmix/openjst.git)
 
**OpenJST** (Open Job Scheduling & Tracking) it is an open source project created to provide highly customized system for tracking and fleet management works in real time.

## Requirements
 * JDK 1.6 or later
 * JBoss AS 7.1.1.Final

### Development:
 * Maven 3
 * Android SDK (Platform 8)
 * Any Java EE 6 compliant IDE

## Quick Start
 
### Download Sources
 
use git
 
```bash
git clone https://github.com/devmix/openjst.git
```

Or download from [Here](https://github.com/devmix/openjst/archive/master.zip)
 
#### Configuring JBoss

`standalone-full` profile is used by default. You need to add the following settings in `<JBoss>/standalone/configuration/standalone-full.xml`

* JAAS

```xml
<subsystem xmlns="urn:jboss:domain:security:1.1">
    ...
    <security-domain name="OpenJSTServerMobileRealm" cache-type="default">
        <authentication>
            <login-module code="org.openjst.server.mobile.jaas.DatabaseLoginModule" flag="required">
                <module-option name="realm" value="OpenJSTServerMobileRealm"/>
        </login-module>
        </authentication>
    </security-domain>
    ...
</security-domains>
```
* Cache

```xml
<subsystem xmlns="urn:jboss:domain:infinispan:1.2" default-cache-container="hibernate">
    ...
    <cache-container name="openjst-cache" default-cache="openjst-local-cache" start="EAGER">
        <local-cache name="openjst-local-cache"/>
        <local-cache name="openjst-web-ui-cache"/>
    </cache-container>
    ...
</subsystem>
```

#### Configuring project

At the initial stage of development as a replacement for the database used H2 Database Engine. You can change location of database in `server-mobile-ds.xml` (by default `~/server-mobile`)

### Build

```bash
mvn clean install -Pserver-mobile,client-android,protocol
```

If you are running jboss, you can deploy application using this command

```bash
mvn jboss-as:deploy -Pserver-mobile
```

#### Sing in

When you first start the JBoss account "system" will be created with user "system". To log in you should use ID = `system@system`, password = `system`.
 
## License
 
Source code can be found on [github](https://github.com/devmix/openjst.git), licenced under [AGPLv3](http://www.gnu.org/licenses/agpl.html).
