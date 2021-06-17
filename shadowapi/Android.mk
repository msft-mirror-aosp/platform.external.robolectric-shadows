LOCAL_PATH := $(call my-dir)

##############################################
# Execute Robolectric shadowapi tests
##############################################
include $(CLEAR_VARS)

LOCAL_MODULE := Run_robolectric_shadowapi_tests
LOCAL_LICENSE_KINDS := SPDX-license-identifier-Apache-2.0
LOCAL_LICENSE_CONDITIONS := notice
LOCAL_NOTICE_FILE := $(LOCAL_PATH)/../LICENSE

test_source_directory := $(LOCAL_PATH)/src/test/java

test_runtime_libraries := \
  Robolectric_shadowapi_tests

include external/robolectric-shadows/run_robolectric_module_tests.mk
