// Copyright (C) 2019 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package robolectric

import (
	"fmt"
	"strings"

	"android/soong/android"
)

var pctx = android.NewPackageContext("android/soong/robolectric")

func init() {
	pctx.Import("android/soong/android")
	android.RegisterModuleType("robolectric_build_props", buildPropsFactory)
}

type buildProps struct {
	android.ModuleBase
	output android.WritablePath
}

var _ android.SourceFileProducer = (*buildProps)(nil)

func (b *buildProps) Srcs() android.Paths {
	return android.Paths{b.output}
}

func (b *buildProps) GenerateAndroidBuildActions(ctx android.ModuleContext) {

	displayID := fmt.Sprintf("robolectric %s %s",
		ctx.Config().PlatformVersionName(),
		ctx.Config().BuildId())

	lines := []string{
		"# build properties autogenerated by robolectric.go",
		"",
		"ro.build.id=" + ctx.Config().BuildId(),
		"ro.build.display.id=" + displayID,
		"ro.product.name=robolectric",
		"ro.product.device=robolectric",
		"ro.product.board=robolectric",
		"ro.product.manufacturer=robolectric",
		"ro.product.brand=robolectric",
		"ro.product.model=robolectric",
		"ro.hardware=robolectric",
		"ro.build.version.security_patch=" + ctx.Config().PlatformSecurityPatch(),
		"ro.build.version.sdk=" + ctx.Config().PlatformSdkVersion().String(),
		"ro.build.version.release=" + ctx.Config().PlatformVersionName(),
		"ro.build.version.preview_sdk=" + ctx.Config().PlatformPreviewSdkVersion(),
		// We don't have the API fingerprint available, just use the preview SDK version.
		"ro.build.version.preview_sdk_fingerprint=" + ctx.Config().PlatformPreviewSdkVersion(),
		"ro.build.version.codename=" + ctx.Config().PlatformSdkCodename(),
		"ro.build.version.all_codenames=" + strings.Join(ctx.Config().PlatformVersionActiveCodenames(), ","),
		"ro.build.version.min_supported_target_sdk=" + ctx.Config().PlatformMinSupportedTargetSdkVersion(),
		"ro.build.version.base_os=" + ctx.Config().PlatformBaseOS(),
		"ro.build.tags=robolectric",
		"ro.build.fingerprint=robolectric",
		"ro.build.characteristics=robolectric",
		"",
		"# for backwards-compatibility reasons, set CPUs to unknown/ARM",
		"ro.product.cpu.abi=unknown",
		"ro.product.cpu.abi2=unknown",
		"ro.product.cpu.abilist=armeabi-v7a",
		"ro.product.cpu.abilist32=armeabi-v7a,armeabi",
		"ro.product.cpu.abilist64=armeabi-v7a,armeabi",
		"",
		"# temp fix for robolectric freezing issue b/150011638",
		"persist.debug.new_insets=0",
	}

	b.output = android.PathForModuleGen(ctx, "build.prop")

	rule := android.NewRuleBuilder()

	rule.Command().Text("rm").Flag("-f").Output(b.output)
	for _, l := range lines {
		rule.Command().Text("echo").Text("'" + l + "'").Text(">>").Output(b.output)
	}

	rule.Build(pctx, ctx, "build_prop", "robolectric build.prop")
}

func buildPropsFactory() android.Module {
	module := &buildProps{}
	android.InitAndroidModule(module)
	return module
}
