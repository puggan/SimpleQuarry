package org.zeith.squarry.init;

import net.minecraft.core.component.DataComponentType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
import org.zeith.squarry.items.data.FilterDataComponent;

@SimplyRegister
public interface ComponentTypesSQ
{
	@RegistryName("filter")
	Registrar<DataComponentType<FilterDataComponent>> FILTER_TYPE = Registrar.dataComponentType(DataComponentType.<FilterDataComponent>builder()
			.cacheEncoding()
			.persistent(FilterDataComponent.CODEC)
			.networkSynchronized(FilterDataComponent.STREAM_CODEC)
	);
}