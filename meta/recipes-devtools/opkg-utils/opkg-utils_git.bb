SUMMARY = "Additional utilities for the opkg package manager"
SUMMARY_update-alternatives-opkg = "Utility for managing the alternatives system"
SECTION = "base"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://opkg.py;beginline=1;endline=18;md5=15917491ad6bf7acc666ca5f7cc1e083"
PROVIDES += "${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'virtual/update-alternatives', '', d)}"

SRCREV = "1a708fd73d10c2b7677dd4cc4e017746ebbb9166"
PV = "0.3.4+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/opkg-utils \
"
SRC_URI_append_class-native = " file://tar_ignore_error.patch"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

PYTHONRDEPS = "python python-shell python-io python-math python-crypt python-logging python-fcntl python-subprocess python-pickle python-compression python-textutils python-stringold"
PYTHONRDEPS_class-native = ""

PACKAGECONFIG = "python update-alternatives"
PACKAGECONFIG[python] = ",,,${PYTHONRDEPS}"
PACKAGECONFIG[update-alternatives] = ",,,"

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'true', 'false', d)}; then
		rm -f "${D}${bindir}/update-alternatives"
	fi
}

do_install_append_class-target() {
	if [ -e "${D}${bindir}/update-alternatives" ]; then
		sed -i ${D}${bindir}/update-alternatives -e 's,/usr/bin,${bindir},g; s,/usr/lib,${nonarch_libdir},g'
	fi
}

PACKAGES =+ "update-alternatives-opkg"
FILES_update-alternatives-opkg = "${bindir}/update-alternatives"
RPROVIDES_update-alternatives-opkg = "update-alternatives update-alternatives-cworth"
RREPLACES_update-alternatives-opkg = "update-alternatives-cworth"
RCONFLICTS_update-alternatives-opkg = "update-alternatives-cworth"

pkg_postrm_update-alternatives-opkg() {
	rm -rf $D${nonarch_libdir}/opkg/alternatives
	rmdir $D${nonarch_libdir}/opkg || true
}

BBCLASSEXTEND = "native nativesdk"

CLEANBROKEN = "1"
